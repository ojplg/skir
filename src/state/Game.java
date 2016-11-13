package state;

import card.Card;
import card.CardStack;
import map.Continent;
import map.Country;
import map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.Callback;
import org.jetlang.fibers.ThreadFiber;
import play.Channels;
import play.Roller;
import play.Rolls;
import state.event.ClientConnectedEvent;
import state.event.MapChangedEvent;
import state.event.PlayerChangedEvent;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class Game {

    private static final Logger _log = LogManager.getLogger(Game.class);

    private WorldMap _map;
    private Occupations _occupations = new Occupations();
    private List<Player> _players = new ArrayList<Player>();
    private CardStack _cardPile;
    private Player _currentAttacker;
    private Roller _roller;
    private Channels _channels;

    private ThreadFiber _fiber = new ThreadFiber();

    public Game(WorldMap map, List<Player> players, List<Card> cards, Roller roller, Channels channels){
        _map = map;
        _players.addAll(players);
        _cardPile = new CardStack(cards);
        _currentAttacker = players.get(0);
        _roller = roller;
        _channels = channels;
        channels.ClientConnectedEventChannel.subscribe(
                _fiber,
                new Callback<ClientConnectedEvent>() {
                    @Override
                    public void onMessage(ClientConnectedEvent clientConnectedEvent) {
                        publishAllState();
                    }
                }
        );
        _fiber.start();
    }

    private void publishAllState(){
        for(Country country : _map.getAllCountries()){
            Player player = _occupations.getOccupier(country);
            int armyCount = _occupations.getOccupationForce(country);
            _channels.MapChangedEventChannel.publish(
                    new MapChangedEvent(country, player, armyCount)
            );
        }
        for(Player player : _players){
            publishPlayerChanged(player);
        }
    }

    public void publishPlayerChanged(Player player){
        int countryCount = numberCountriesOccupied(player);
        int armyCount = _occupations.totalOccupationForces(player) + player.reserveCount();
        int cardCount = player.getCards().size();
        int continentCount = numberContinentsOccupied(player);
        _channels.PlayerChangedEventChannel.publish(
                new PlayerChangedEvent(player, countryCount, armyCount, cardCount, continentCount)
        );
    }

    public void doInitialPlacements(){
        for(Player player : _players) {
            int index = 0;
            List<Country> countries = _occupations.countriesOccupied(player);
            while (player.hasReserves()){
                placeArmy(player, countries.get(index));
                index++;
                index = index % countries.size();
            }
        }
    }

    public boolean hasLegalFortification(Player player){
        return possibleFortificationCountries(player).size() > 0;
    }

    public List<Country> possibleFortificationCountries(Player player){
        List<Country> sources = new ArrayList<Country>();
        for(Country country : _occupations.countriesOccupied(player)){
            if(_occupations.getOccupationForce(country) > 1){
                if(alliedNeighbors(country).size() > 0){
                    sources.add(country);
                }
            }
        }
        return sources;
    }

    public Player currentAttacker(){
        return _currentAttacker;
    }

    public Player nextPlayer(){
        _log.info("Next player called while player is " + _currentAttacker);
        int playerCount = _players.size();
        int currentPlayerIndex = _players.indexOf(_currentAttacker);
        int nextPlayerIndex = (currentPlayerIndex + 1) % playerCount;
        _log.info("next player index: " + nextPlayerIndex);
        _currentAttacker = _players.get(nextPlayerIndex);
        _log.info("active player set to " + _currentAttacker);
        return _currentAttacker;
    }

    public int computeMapSupply(Player player){
        return computeCountrySupply(player) + computeContinentSupply(player);
    }

    public int computeContinentSupply(Player player){
        int cnt = 0;
        for( Continent continent : continentsOccupied(player)){
            cnt += continent.getBonus();
        }
        return cnt;
    }

    private int computeCountrySupply(Player player) {
        return Math.max(3, numberCountriesOccupied(player) / 3);
    }

    public int numberCountriesOccupied(Player player){
        return countriesOccupied(player).size();
    }

    public int numberContinentsOccupied(Player player){
        return continentsOccupied(player).size();
    }

    /** returns true if a country is conquered, false otherwise */
    public boolean resolveAttack(Country attacker, Country defender){
        if (! isTarget(attacker, defender) ){
            throw new RuntimeException("Cannot attack " + defender.getName() + " from " + attacker.getName());
        }
        int attackerDice = Math.min(3, _occupations.getOccupationForce(attacker) - 1);
        int defenderDice = Math.min(2, _occupations.getOccupationForce(defender));
        Rolls rolls = _roller.roll(attackerDice, defenderDice);
        _occupations.killArmies(attacker, rolls.attackersLosses());
        _occupations.killArmies(defender, rolls.defendersLosses());
        notifyListenersOfMapUpdate(attacker);
        notifyListenersOfMapUpdate(defender);
        Player attackingPlayer = _occupations.getOccupier(attacker);
        Player defendingPlayer = _occupations.getOccupier(defender);
        publishPlayerChanged(attackingPlayer);
        publishPlayerChanged(defendingPlayer);
        return _occupations.allArmiesDestroyed(defender);
    }

    /** returns true if a player has been eliminated */
    public boolean resolveConquest(Country conqueror, Country vanquished, int occupyingArmyCount){
        if (! _occupations.allArmiesDestroyed(vanquished)) {
            throw new RuntimeException(vanquished.getName() + " still has " + _occupations.getOccupationForce(vanquished) + " armies in it");
        }
        if( occupyingArmyCount > _occupations.getOccupationForce(conqueror) - 1){
            throw new RuntimeException(conqueror.getName() + " only has " + _occupations.getOccupationForce(conqueror) + " armies in it");
        }
        Player defender = _occupations.getOccupier(vanquished);
        // really just reducing the count here ... but the kill method works
        _occupations.killArmies(conqueror, occupyingArmyCount);
        Player attacker = _occupations.getOccupier(conqueror);
        _occupations.placeArmies(attacker, vanquished, occupyingArmyCount);
        notifyListenersOfMapUpdate(conqueror);
        notifyListenersOfMapUpdate(vanquished);
        return countriesOccupied(defender).size() == 0;
    }

    /** returns true if the game is over */
    public boolean resolveElimination(Player conqueror, Player vanquished){
        List<Card> cards = vanquished.getCards();
        vanquished.removeCards(cards);
        conqueror.addCards(cards);
        _players.remove(vanquished);
        publishPlayerChanged(conqueror);
        publishPlayerChanged(vanquished);
        return _players.size() == 1;
    }

    public boolean gameWon(){
        return _players.size() == 0;
    }

    public List<Country> countriesOccupied(Player player){
        return _occupations.countriesOccupied(player);
    }

    public List<Country> countriesToAttackFrom(Player player){
        List<Country> attackBases = new ArrayList<Country>();
        for(Country country : countriesOccupied(player)){
            if (_occupations.hasAttackingForces(country)){
                List<Country> neighbors = _map.getNeighbors(country);
                if( _occupations.hasEnemy(country, neighbors)){
                    attackBases.add(country);
                }
            }
        }
        return attackBases;
    }

    public List<Continent> continentsOccupied(Player player){
        List<Continent> occupied = new ArrayList<Continent>();
        for (Continent continent : _map.getContinents() ){
            if( continentOccupied(player, continent)){
                occupied.add(continent);
            }
        }
        return occupied;
    }

    public boolean continentOccupied(Player player, Continent continent){
        int cnt = 0;
        for ( Country country : continent.getCountries() ){
            if ( player.equals(getOccupier(country)) ) {
                cnt++;
            }
        }
        return cnt == continent.numberCountries();
    }

    public List<Country> enemyNeighbors(Country country){
        return filterCountries(country, false);
    }

    public List<Country> alliedNeighbors(Country country){
        return filterCountries(country, true);
    }

    private List<Country> filterCountries(Country country, boolean sameOccupier){
        List<Country> filtered = new ArrayList<Country>();
        Player occupier = getOccupier(country);
        for (Country neighbor : _map.getNeighbors(country)){
            if(sameOccupier && occupier.equals(getOccupier(neighbor)) ) {
                filtered.add(neighbor);
            } else if ( ! sameOccupier && ! occupier.equals(getOccupier(neighbor))) {
                filtered.add(neighbor);
            }
        }
        return filtered;
    }

    public boolean isTarget(Country attacker, Country defender){
        if( _map.areNeighbors(attacker, defender)){
            return ! getOccupier(attacker).equals(getOccupier(defender));
        }
        return false;
    }

    public Player getOccupier(Country country){
        return _occupations.getOccupier(country);
    }

    public int getOccupationForce(Country country){
        return _occupations.getOccupationForce(country);
    }

    public void placeArmy(Player player, Country country) {
        placeArmies(player, country, 1);
    }

    public void placeArmies(Player player, Country country, int count){
        player.drawReserves(count);
        _occupations.placeArmies(player, country, count);
        notifyListenersOfMapUpdate(country);
    }

    private void notifyListenersOfMapUpdate(Country country){
        int newCount = _occupations.getOccupationForce(country);
        Player player = _occupations.getOccupier(country);

        MapChangedEvent event = new MapChangedEvent(country, player, newCount);
        _channels.MapChangedEventChannel.publish(event);
    }

    public void fortify(Country source, Country destination, int armies){
        // TODO: check for constraints
        Player player = getOccupier(source);
        _occupations.killArmies(source, armies);
        _occupations.placeArmies(player, destination, armies);
        notifyListenersOfMapUpdate(source);
        notifyListenersOfMapUpdate(destination);
    }

    public Card drawCard() {
        return _cardPile.drawCard();
    }

    public int tradeCards(Card one, Card two, Card three){
        List<Card> cards = new ArrayList<Card>();
        cards.add(one);
        cards.add(two);
        cards.add(three);
        _currentAttacker.removeCards(cards);
        return _cardPile.tradeCards(one, two, three);
    }

    public List<Country> getAllCountries(){
        return _map.getAllCountries();
    }

    public List<Player> getAllPlayers(){
        return _players;
    }

    public String toString(){
        StringBuilder sbuf = new StringBuilder();

        for ( Continent con : _map.getContinents()) {
            sbuf.append(con.getName());
            sbuf.append("\n");
            List<Country> countries = con.getCountries();
            for (Country ctry : countries){
                sbuf.append(formatCountry(ctry));
            }
        }
        for ( Player plyr : _players ){
            sbuf.append(plyr);
            sbuf.append("\n");
        }

        return sbuf.toString();
    }

    private String formatCountry(Country country){
        Formatter formatter = new Formatter();
        Player player = getOccupier(country);
        int armies = getOccupationForce(country);

        formatter.format("  %-25s %-10s %4d \n", country.getName(), player.getColor(), armies);

        return formatter.toString();
    }
}
