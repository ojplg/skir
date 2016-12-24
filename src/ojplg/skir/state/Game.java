package ojplg.skir.state;

import ojplg.skir.card.Card;
import ojplg.skir.card.CardSet;
import ojplg.skir.card.CardStack;
import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.map.WorldMap;
import ojplg.skir.play.Channels;
import ojplg.skir.play.Roller;
import ojplg.skir.play.Rolls;
import ojplg.skir.play.Skir;
import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.MapChangedEvent;
import ojplg.skir.state.event.PlayerChangedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.ThreadFiber;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Game {

    private static final Logger _log = LogManager.getLogger(Game.class);

    private final Occupations _occupations;
    private final List<Player> _players = new ArrayList<>();
    private final CardStack _cardPile;
    private final Roller _roller;
    private final Channels _channels;

    private final ThreadFiber _fiber = Skir.createThreadFiber("GameRunnerFiber");

    private Player _currentAttacker;
    private int _turnNumber = 1;
    private int _lastAttackTurn = 0;

    public Game(WorldMap map, List<Player> players, List<Card> cards, Roller roller, Channels channels){
        this(players, cards, roller, channels, new Occupations(map));
    }

    public Game(List<Player> players, List<Card> cards, Roller roller, Channels channels, Occupations occupations){
        _players.addAll(players);
        _cardPile = new CardStack(cards);
        _roller = roller;
        _channels = channels;
        _occupations = occupations;
    }

    public void start(){
        _currentAttacker = _players.get(0);
        _fiber.start();
    }

    public void doInitialPlacements(){
        for(Player player : _players) {
            int index = 0;
            List<Country> countries = _occupations.countriesOccupied(player);
            while (player.hasReserves()){
                processPlaceArmyOrder(player, countries.get(index), 1);
                index++;
                index = index % countries.size();
            }
        }
    }

    public int getTurnNumber(){
        return _turnNumber;
    }

    public Player currentAttacker(){
        return _currentAttacker;
    }

    public boolean hasLegalFortification(Player player){
        return possibleFortificationCountries(player).size() > 0;
    }

    public List<Country> possibleFortificationCountries(Player player){
        return _occupations.countriesOccupied(player).stream()
                .filter(country -> _occupations.getOccupationForce(country) > 1)
                .filter(country -> findAlliedNeighbors(country).size() > 0)
                .collect(Collectors.toList());
    }

    public Player nextPlayer(){
        _log.info("Next player called while player is " + _currentAttacker);
        int playerCount = _players.size();
        int currentPlayerIndex = _players.indexOf(_currentAttacker);
        int nextPlayerIndex = (currentPlayerIndex + 1) % playerCount;
        if( nextPlayerIndex == 0 ){
            _turnNumber++;
            _log.info("Turn number " + _turnNumber);
        }
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
        return findContinentsOccupied(player).stream()
                .map(Continent::getBonus)
                .reduce(0, Integer::sum);
    }

    private int computeCountrySupply(Player player) {
        return Math.max(Constants.MINIMUM_ARMY_GRANT,numberCountriesOccupied(player) / 3);
    }

    private int numberCountriesOccupied(Player player){
        return findOccupiedCountries(player).size();
    }

    private int numberContinentsOccupied(Player player){
        return findContinentsOccupied(player).size();
    }

    /** returns true if a country is conquered, false otherwise */
    public boolean processAttackOrder(Country attacker, Country defender){
        if (! isTarget(attacker, defender) ){
            throw new RuntimeException("Cannot attack " + defender.getName() + " from " + attacker.getName());
        }
        _lastAttackTurn = _turnNumber;
        // TODO: allow players to choose number of dice
        int attackerDice = Math.min(Constants.MAXIMUM_ATTACKER_DICE, _occupations.getOccupationForce(attacker) - 1);
        int defenderDice = Math.min(Constants.MAXIMUM_DEFENDER_DICE, _occupations.getOccupationForce(defender));
        Rolls rolls = _roller.roll(attackerDice, defenderDice);
        _occupations.killArmies(attacker, rolls.attackersLosses());
        _occupations.killArmies(defender, rolls.defendersLosses());
        publishCountryState(attacker);
        publishCountryState(defender);
        Player attackingPlayer = _occupations.getOccupier(attacker);
        attackingPlayer.updateAttackStatistics(rolls.attackersExpectationsDifference(), rolls.numberBattles());
        Player defendingPlayer = _occupations.getOccupier(defender);
        defendingPlayer.updateDefenseStatistics(rolls.defendersExpectationsDifference(), rolls.numberBattles());
        publishPlayerState(attackingPlayer);
        publishPlayerState(defendingPlayer);
        _channels.GameEventChannel.publish(GameEvent.forAttack(currentAttacker(), attacker, defender));
        return _occupations.allArmiesDestroyed(defender);
    }

    /** returns true if a player has been eliminated */
    public boolean processOccupyOrder(Country conqueror, Country vanquished, int occupyingArmyCount){
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
        publishCountryState(conqueror);
        publishCountryState(vanquished);
        _channels.GameEventChannel.publish(GameEvent.forOccupy(currentAttacker(), conqueror, vanquished));
        boolean defenderEliminated =  findOccupiedCountries(defender).size() == 0;
        if (defenderEliminated){
            _channels.GameEventChannel.publish(GameEvent.eliminated(defender, _turnNumber));
        }
        return defenderEliminated;
    }

    /** returns true if the game is over */
    public boolean resolveElimination(Player conqueror, Player vanquished){
        List<Card> cards = vanquished.getCards();
        vanquished.removeCards(cards);
        conqueror.addCards(cards);
        _log.info("Removing player " + vanquished);
        _players.remove(vanquished);
        _log.info("Player count is " +_players.size());
        publishPlayerState(conqueror);
        publishPlayerState(vanquished);
        boolean gameOver = _players.size() == 1;
        if ( gameOver ){
            _channels.GameEventChannel.publish(GameEvent.wins(conqueror, _turnNumber));
        }
        return gameOver;
    }

    public boolean gameOver() {
        if( _players.size() <= 1 ){
            return true;
        }
        if( _turnNumber - _lastAttackTurn >= Constants.MAX_TURNS_WITHOUT_ATTACK
                || _turnNumber > Constants.MAXIMUM_GAME_LENGTH ) {
            _channels.GameEventChannel.publish(GameEvent.draw(getTurnNumber(),
                    _players.stream().map(p -> p.getDisplayName()).collect(Collectors.toList())));
            return true;
        }
        return false;
    }

    public List<Country> findOccupiedCountries(Player player){
        return _occupations.countriesOccupied(player);
    }

    public boolean hasPossibleAttack(Player player){
        return findCountriesToAttackFrom(player).size() > 0;
    }

    public List<Country> findCountriesToAttackFrom(Player player){
        return filter(findBorderCountries(player), _occupations::hasAttackingForces);
    }

    public List<Country> findBorderCountries(Player player){
        return filter(findOccupiedCountries(player), _occupations::hasEnemyNeighbor);
    }

    private static <T> List<T> filter(List<T> items, Predicate<? super T> predicate){
        return items.stream().filter(predicate).collect(Collectors.toList());
    }

    public List<Country> findInteriorCountries(Player player){
        return filter(findOccupiedCountries(player), c -> ! _occupations.hasEnemyNeighbor(c));
    }

    private List<Continent> findContinentsOccupied(Player player){
        return filter(getAllContinents(), continent -> continentOccupied(player, continent));
    }

    private boolean continentOccupied(Player player, Continent continent){
        return continent.getCountries().stream()
                .allMatch(country -> player.equals(getOccupier(country)));
    }

    public List<Country> findEnemyNeighbors(Country country){
        return _occupations.enemyNeighbors(country);
    }

    public List<Country> findAlliedNeighbors(Country country){
        return _occupations.alliedNeighbors(country);
    }

    public List<Country> findAllNeighbors(Country country){
        return _occupations.getMap().getNeighbors(country);
    }

    private boolean isTarget(Country attacker, Country defender){
        if( _occupations.getMap().areNeighbors(attacker, defender)){
            return ! getOccupier(attacker).equals(getOccupier(defender));
        }
        return false;
    }

    public void processPlaceArmyOrder(Player player, Country country, int count){
        player.drawReserves(count);
        _occupations.placeArmies(player, country, count);
        publishCountryState(country);
    }

    public void processFortifyOrder(Country source, Country destination, int armies){
        // TODO: check for constraints
        Player player = getOccupier(source);
        _occupations.killArmies(source, armies);
        _occupations.placeArmies(player, destination, armies);
        _channels.GameEventChannel.publish(GameEvent.forFortify(currentAttacker(), source, destination));
        publishCountryState(source);
        publishCountryState(destination);
    }

    public Card processDrawCardOrder() {
        return _cardPile.drawCard();
    }

    public int processExchangeCardSetOrder(CardSet set){
        _currentAttacker.removeCards(set.asList());
        int bonusArmies = _cardPile.tradeCards(set);
        set.asList().forEach(this::applyCardCountryBonus);
        _channels.GameEventChannel.publish(GameEvent.forCardExchange(currentAttacker()));
        return bonusArmies;
    }

    private void applyCardCountryBonus(Card card){
        if(_currentAttacker.equals(getOccupier(card.getCountry()))){
            _occupations.placeArmies(_currentAttacker, card.getCountry(),
                    Constants.CARD_COUNTRY_BONUS);
            publishCountryState(card.getCountry());
        }
    }

    public void publishAllState(){
        getAllCountries().forEach(this::publishCountryState);
        _players.forEach(this::publishPlayerState);
    }

    public void publishPlayerState(Player player){
        int countryCount = numberCountriesOccupied(player);
        int armyCount = _occupations.totalOccupationForces(player) + player.reserveCount();
        int continentCount = numberContinentsOccupied(player);
        int expectedGrant = computeExpectedGrant(player);
        _channels.PlayerChangedEventChannel.publish(
                new PlayerChangedEvent(player, countryCount, armyCount, continentCount, expectedGrant)
        );
    }

    private void publishCountryState(Country country){
        int newCount = _occupations.getOccupationForce(country);
        Player player = _occupations.getOccupier(country);

        MapChangedEvent event = new MapChangedEvent(country, player, newCount);
        _channels.MapChangedEventChannel.publish(event);
    }

    public List<Country> getAllCountries(){
        return _occupations.getMap().getAllCountries();
    }

    public List<Continent> getAllContinents(){
        return _occupations.getMap().getContinents();
    }

    public List<Player> getAllPlayers(){
        return _players;
    }

    public Player getOccupier(Country country){
        return _occupations.getOccupier(country);
    }

    public int getOccupationForce(Country country){
        return _occupations.getOccupationForce(country);
    }

    public WorldMap getMap() {
        return _occupations.getMap();
    }

    public String toString(){
        StringBuilder sbuf = new StringBuilder();

        for ( Continent con : _occupations.getMap().getContinents()) {
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
