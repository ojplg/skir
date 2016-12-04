package ojplg.skir.state;

import ojplg.skir.card.Card;
import ojplg.skir.card.CardStack;
import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.map.WorldMap;
import ojplg.skir.state.event.OrderEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.ThreadFiber;
import ojplg.skir.play.Channels;
import ojplg.skir.play.Roller;
import ojplg.skir.play.Rolls;
import ojplg.skir.state.event.MapChangedEvent;
import ojplg.skir.state.event.PlayerChangedEvent;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;

public class Game {

    private static final Logger _log = LogManager.getLogger(Game.class);

    private final WorldMap _map;
    private final Occupations _occupations = new Occupations();
    private final List<Player> _players = new ArrayList<>();
    private final CardStack _cardPile;
    private final Roller _roller;
    private final Channels _channels;
    private final ThreadFiber _fiber = new ThreadFiber();

    private Player _currentAttacker;

    public Game(WorldMap map, List<Player> players, List<Card> cards, Roller roller, Channels channels){
        _map = map;
        _players.addAll(players);
        _cardPile = new CardStack(cards);
        _roller = roller;
        _channels = channels;
    }

    public void start(){
        _currentAttacker = _players.get(0);
        _fiber.start();
    }

    public void publishAllState(){
        for(Country country : _map.getAllCountries()){
            Player player = _occupations.getOccupier(country);
            int armyCount = _occupations.getOccupationForce(country);
            _channels.MapChangedEventChannel.publish(
                    new MapChangedEvent(country, player, armyCount)
            );
        }
        _players.forEach(this::publishPlayerChanged);
    }

    public void publishPlayerChanged(Player player){
        int countryCount = numberCountriesOccupied(player);
        int armyCount = _occupations.totalOccupationForces(player) + player.reserveCount();
        int continentCount = numberContinentsOccupied(player);
        int expectedGrant = computeExpectedGrant(player);
        _channels.PlayerChangedEventChannel.publish(
                new PlayerChangedEvent(player, countryCount, armyCount, continentCount, expectedGrant)
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
        return _occupations.countriesOccupied(player).stream()
                .filter(country -> _occupations.getOccupationForce(country) > 1)
                .filter(country -> alliedNeighbors(country).size() > 0)
                .collect(Collectors.toList());
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

    private int computeMapSupply(Player player){
        return computeCountrySupply(player) + computeContinentSupply(player);
    }

    public int computeExpectedGrant(Player player){
        return computeMapSupply(player);
    }

    private int computeContinentSupply(Player player){
        int cnt = 0;
        for( Continent continent : continentsOccupied(player)){
            cnt += continent.getBonus();
        }
        return cnt;
    }

    private int computeCountrySupply(Player player) {
        return Math.max(Constants.MINIMUM_ARMY_GRANT,numberCountriesOccupied(player) / 3);
    }

    private int numberCountriesOccupied(Player player){
        return countriesOccupied(player).size();
    }

    private int numberContinentsOccupied(Player player){
        return continentsOccupied(player).size();
    }

    /** returns true if a country is conquered, false otherwise */
    public boolean resolveAttack(Country attacker, Country defender){
        if (! isTarget(attacker, defender) ){
            throw new RuntimeException("Cannot attack " + defender.getName() + " from " + attacker.getName());
        }
        // TODO: allow players to choose number of dice
        int attackerDice = Math.min(Constants.MAXIMUM_ATTACKER_DICE, _occupations.getOccupationForce(attacker) - 1);
        int defenderDice = Math.min(Constants.MAXIMUM_DEFENDER_DICE, _occupations.getOccupationForce(defender));
        Rolls rolls = _roller.roll(attackerDice, defenderDice);
        _occupations.killArmies(attacker, rolls.attackersLosses());
        _occupations.killArmies(defender, rolls.defendersLosses());
        notifyListenersOfMapUpdate(attacker);
        notifyListenersOfMapUpdate(defender);
        Player attackingPlayer = _occupations.getOccupier(attacker);
        attackingPlayer.updateAttackStatistics(rolls.attackersExpectationsDifference(), rolls.numberBattles());
        Player defendingPlayer = _occupations.getOccupier(defender);
        defendingPlayer.updateDefenseStatistics(rolls.defendersExpectationsDifference(), rolls.numberBattles());
        publishPlayerChanged(attackingPlayer);
        publishPlayerChanged(defendingPlayer);
        _channels.OrderEventChannel.publish(OrderEvent.forAttack(currentAttacker(), attacker, defender));
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
        _channels.OrderEventChannel.publish(OrderEvent.forOccupy(currentAttacker(), conqueror, vanquished));
        return countriesOccupied(defender).size() == 0;
    }

    /** returns true if the game is over */
    public boolean resolveElimination(Player conqueror, Player vanquished){
        List<Card> cards = vanquished.getCards();
        vanquished.removeCards(cards);
        conqueror.addCards(cards);
        _log.info("Removing player " + vanquished);
        _players.remove(vanquished);
        _log.info("Player count is " +_players.size());
        publishPlayerChanged(conqueror);
        publishPlayerChanged(vanquished);
        return _players.size() == 1;
    }

    public boolean gameOver(){
        return _players.size() <= 1;
    }

    public List<Country> countriesOccupied(Player player){
        return _occupations.countriesOccupied(player);
    }

    public List<Country> countriesToAttackFrom(Player player){
        return borderCountries(player).stream()
                .filter(border -> _occupations.getOccupationForce(border) > 1)
                .collect(Collectors.toList());
    }

    public List<Country> borderCountries(Player player){
        List<Country> borders = new ArrayList<Country>();
        for(Country country : countriesOccupied(player)){
            List<Country> neighbors = _map.getNeighbors(country);
            if( _occupations.hasEnemy(country, neighbors)){
                borders.add(country);
            }
        }
        return borders;
    }

    private List<Continent> continentsOccupied(Player player){
        return _map.getContinents().stream()
                .filter(continent -> continentOccupied(player, continent))
                .collect(Collectors.toList());
    }

    private boolean continentOccupied(Player player, Continent continent){
        return continent.getCountries().stream()
                .allMatch(country -> player.equals(getOccupier(country)));
    }

    public List<Country> enemyNeighbors(Country country){
        return filterCountries(country, false);
    }

    public List<Country> alliedNeighbors(Country country){
        return filterCountries(country, true);
    }

    private List<Country> filterCountries(Country country, boolean sameOccupier){
        List<Country> filtered = new ArrayList<>();
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

    private boolean isTarget(Country attacker, Country defender){
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
        _channels.OrderEventChannel.publish(OrderEvent.forFortify(currentAttacker(), source, destination));
        notifyListenersOfMapUpdate(source);
        notifyListenersOfMapUpdate(destination);
    }

    public Card drawCard() {
        return _cardPile.drawCard();
    }

    public int tradeCards(Card one, Card two, Card three){
        List<Card> cards = new ArrayList<>();
        cards.add(one);
        cards.add(two);
        cards.add(three);
        _currentAttacker.removeCards(cards);
        int bonusArmies = _cardPile.tradeCards(one, two, three);
        applyCardCountryBonus(one);
        applyCardCountryBonus(two);
        applyCardCountryBonus(three);
        _channels.OrderEventChannel.publish(OrderEvent.forCardExchange(currentAttacker()));
        return bonusArmies;
    }

    private void applyCardCountryBonus(Card card){
        if(_currentAttacker.equals(getOccupier(card.getCountry()))){
            _occupations.placeArmies(_currentAttacker, card.getCountry(),
                    Constants.CARD_COUNTRY_BONUS);
            notifyListenersOfMapUpdate(card.getCountry());
        }
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
