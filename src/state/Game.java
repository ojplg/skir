package state;

import ai.AutomatedPlayer;
import card.Card;
import card.CardStack;
import map.Continent;
import map.Country;
import map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import play.Roller;
import play.Rolls;

import java.util.*;

public class Game implements SignalReady {

    private static final Logger _log = LogManager.getLogger(Game.class);

    private WorldMap _map;
    private Occupations _occupations = new Occupations();
    private List<Player> _players = new ArrayList<Player>();
    private CardStack _cardPile;
    private Player _currentAttacker;
    private List<GameEventListener> _gameEventListeners = new ArrayList<GameEventListener>();
    private Roller _roller;
    private Map<Player,AutomatedPlayer> _automatedPlayers = new HashMap<Player,AutomatedPlayer>();

    public Game(WorldMap map, List<Player> players, List<Card> cards, Roller roller){
        _map = map;
        _players.addAll(players);
        _cardPile = new CardStack(cards);
        _currentAttacker = players.get(0);
        _roller = roller;
    }

    public void addAutomatedPlayer(AutomatedPlayer ai){
        _log.info("Adding automated player for " + ai.getPlayer());
        _automatedPlayers.put(ai.getPlayer(),ai);
    }

    public AutomatedPlayer getAutomatedPlayer(Player player){
        AutomatedPlayer ai = _automatedPlayers.get(player);
        if( ai == null ){
            _log.info("NO AI FOR " + player);
        }
        return ai;
    }

    public Roller getRoller(){
        return _roller;
    }

    public void signal(String flag){
        for(GameEventListener listener : _gameEventListeners){
            if(listener.getId().equals(flag)){
                for(Country country : _map.getAllCountries()){
                    Player player = _occupations.getOccupier(country);
                    int armyCount = _occupations.getOccupationForce(country);
                    listener.mapChanged(country, player, armyCount);
                }
                for(Player player : _players){
                    int countryCount = numberCountriesOccupied(player);
                    int armyCount = _occupations.totalOccupationForces(player);
                    listener.playerChanged(player, armyCount, countryCount);
                }
            }
        }
    }

    public void addMapEventListener(GameEventListener listener){
        _gameEventListeners.add(listener);
    }

    public void doInitialPlacements(){
        for(Player player : _players) {
            notifyListenersOfPlayerUpdate(player);
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
                if(allies(country).size() > 0){
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
    public boolean resolveAttack(Country attacker, Country defender, Rolls rolls){
        if (! isTarget(attacker, defender) ){
            throw new RuntimeException("Cannot attack " + defender.getName() + " from " + attacker.getName());
        }
        _occupations.killArmies(attacker, rolls.attackersLosses());
        _occupations.killArmies(defender, rolls.defendersLosses());
        notifyListenersOfMapUpdate(attacker);
        notifyListenersOfMapUpdate(defender);
        Player attackingPlayer = _occupations.getOccupier(attacker);
        Player defendingPlayer = _occupations.getOccupier(defender);
        notifyListenersOfPlayerUpdate(attackingPlayer);
        notifyListenersOfPlayerUpdate(defendingPlayer);
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

    // returns true if the game is over */
    public boolean resolveElimination(Player conqueror, Player vanquished){
        List<Card> cards = vanquished.getCards();
        vanquished.removeCards(cards);
        conqueror.addCards(cards);
        _players.remove(vanquished);
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

    public List<Country> targets(Country country){
        return filterCountries(country, false);
    }

    public List<Country> allies(Country country){
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
        for(GameEventListener listener : _gameEventListeners){
            if( listener != null) {
                listener.mapChanged(country, player, newCount);
            } else {
                _log.warn("WARNING NULL MAP LISTENER");
            }
        }
    }

    private void notifyListenersOfPlayerUpdate(Player player){
        int countryCount = _occupations.countriesOccupied(player).size();
        int armyCount = _occupations.totalOccupationForces(player);
        armyCount += player.reserveCount();

        for(GameEventListener listener : _gameEventListeners){
            if( listener != null) {
                listener.playerChanged(player, armyCount, countryCount);
            } else {
                _log.warn("WARNING NULL MAP LISTENER");
            }
        }
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
