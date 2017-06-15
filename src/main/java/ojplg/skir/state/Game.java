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
import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.MapChangedEvent;
import ojplg.skir.state.event.PlayerChangedEvent;
import ojplg.skir.utils.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Game {

    private static final Logger _log = LogManager.getLogger(Game.class);

    private final Occupations _occupations;
    private final List<Player> _players = new ArrayList<>();
    private final Map<String, PlayerHoldings> _playerHoldings = new HashMap<>();
    private final CardStack _cardPile;
    private final Roller _roller;
    private final Channels _channels;
    private final int _gameId;
    private static volatile int _gameCount = 0;

    private Player _currentAttacker;
    private int _turnNumber = 1;
    private int _lastAttackTurn = 0;


    public Game(WorldMap map, List<Player> players, List<Card> cards, Roller roller, Channels channels, int initialArmies){
        this(players, cards, roller, channels, new Occupations(map), initialArmies);
    }

    public Game(List<Player> players, List<Card> cards, Roller roller, Channels channels, Occupations occupations, int initialArmies){
        _players.addAll(players);
        _players.forEach(p -> _playerHoldings.put(p.getColor(), new PlayerHoldings(initialArmies)));
        _cardPile = new CardStack(cards);
        _roller = roller;
        _channels = channels;
        _occupations = occupations;
        _gameId = _gameCount++;
    }

    public void start(){
        _log.info("Starting game " + _gameId);
        _currentAttacker = _players.get(0);
    }

    public void doInitialPlacements(){
        for(Player player : _players) {
            int index = 0;
            List<Country> countries = _occupations.countriesOccupied(player);
            while (getPlayerHoldings(player).hasReserves()){
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
        int playerCount = _players.size();
        int currentPlayerIndex = _players.indexOf(_currentAttacker);
        Player oldAttacker = _currentAttacker;
        int nextPlayerIndex = (currentPlayerIndex + 1) % playerCount;
        if( nextPlayerIndex == 0 ){
            _turnNumber++;
            _log.info(_gameId + " turn number "  + _turnNumber);
            logPlayerStatuses();
        }
        _currentAttacker = _players.get(nextPlayerIndex);
        _log.debug("nextPlayer from " + oldAttacker + " to " + _currentAttacker + " index " + nextPlayerIndex +
                " in turn " + _turnNumber);
        return _currentAttacker;
    }

    private void logPlayerStatuses(){
        for(Player player : _players){
            _log.debug(generatePlayerChangedEvent(player));
        }
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
        Player attackingPlayer = _occupations.getOccupier(attacker);
        Player defendingPlayer = _occupations.getOccupier(defender);
        // TODO: allow players to choose number of dice
        int attackerDice = Math.min(Constants.MAXIMUM_ATTACKER_DICE, _occupations.getOccupationForce(attacker) - 1);
        int defenderDice = Math.min(Constants.MAXIMUM_DEFENDER_DICE, _occupations.getOccupationForce(defender));
        Rolls rolls = _roller.roll(attackerDice, defenderDice);
        attackingPlayer.updateAttackStatistics(rolls.attackersExpectationsDifference(), rolls.numberBattles());
        defendingPlayer.updateDefenseStatistics(rolls.defendersExpectationsDifference(), rolls.numberBattles());
        _occupations.killArmies(attacker, rolls.attackersLosses());
        _occupations.killArmies(defender, rolls.defendersLosses());
        publishState(new Player[] { attackingPlayer, defendingPlayer}, new Country[]{attacker, defender});
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
        publishState(new Player[] {attacker, defender}, new Country[]{conqueror, vanquished});
        _channels.GameEventChannel.publish(GameEvent.forOccupy(currentAttacker(), conqueror, vanquished));
        boolean defenderEliminated =  findOccupiedCountries(defender).size() == 0;
        if (defenderEliminated){
            _channels.GameEventChannel.publish(GameEvent.eliminated(defender, _turnNumber));
        }
        return defenderEliminated;
    }

    /** returns true if the game is over */
    public boolean resolveElimination(Player conqueror, Player vanquished){
        List<Card> cards = getPlayerHoldings(vanquished).getCards();
        getPlayerHoldings(vanquished).removeCards(cards);
        getPlayerHoldings(conqueror).addCards(cards);
        _players.remove(vanquished);
        _log.info("Removed player " + vanquished + " new player count is " +_players.size() + " in turn " + _turnNumber);
        publishState(new Player[] { conqueror, vanquished}, new Country[0]);
        boolean gameOver = _players.size() == 1;
        if ( gameOver ){
            _channels.GameEventChannel.publish(GameEvent.wins(conqueror, _turnNumber));
        }
        return gameOver;
    }
    
    public void processPlaceArmyOrder(Player player, Country country, int count){
        getPlayerHoldings(player).drawReserves(count);
        _occupations.placeArmies(player, country, count);
        publishCountryState(country);
    }

    public void processFortifyOrder(Country source, Country destination, int armies){
        Player sourcePlayer = getOccupier(source);
        Player destinationPlayer = getOccupier(destination);
        if ( ! sourcePlayer.equals(destinationPlayer)){
            throw new RuntimeException("Cannot fortify from " + source + " to " + destination + ". Different owners.");
        }
        if( ! getMap().areNeighbors(source, destination)){
            throw new RuntimeException("Cannot fortify from " + source + " to " + destination + ". Not neighbors.");
        }
        int currentArmies = getOccupationForce(source);
        if (armies >= currentArmies){
            throw new RuntimeException("Cannot fortify " + armies +  " from " + source);
        }
        _occupations.killArmies(source, armies);
        _occupations.placeArmies(sourcePlayer, destination, armies);
        _channels.GameEventChannel.publish(GameEvent.forFortify(currentAttacker(), source, destination));
        publishCountryState(source);
        publishCountryState(destination);
    }

    public Card processDrawCardOrder() {
        Card card =  _cardPile.drawCard();
        publishPlayerState(_currentAttacker);
        return card;
    }

    public void processExchangeCardSetOrder(CardSet set){
        getPlayerHoldings(_currentAttacker).removeCards(set.asList());
        int bonusArmies = _cardPile.tradeCards(set);
        set.asList().forEach(this::applyCardCountryBonus);
        _channels.GameEventChannel.publish(GameEvent.forCardExchange(currentAttacker()));
        getPlayerHoldings(_currentAttacker).grantReserves(bonusArmies);
        publishPlayerState(_currentAttacker);
    }

    private void applyCardCountryBonus(Card card){
        if(_currentAttacker.equals(getOccupier(card.getCountry()))){
            _occupations.placeArmies(_currentAttacker, card.getCountry(),
                    Constants.CARD_COUNTRY_BONUS);
            publishCountryState(card.getCountry());
        }
    }

    public boolean gameOver() {
        boolean isOver = false;
        if( _players.size() <= 1 ){
            isOver = true;
        }
        if( _turnNumber - _lastAttackTurn >= Constants.MAX_TURNS_WITHOUT_ATTACK
                || _turnNumber > Constants.MAXIMUM_GAME_LENGTH ) {
            _channels.GameEventChannel.publish(GameEvent.draw(getTurnNumber(),
                    _players.stream().map(p -> p.getDisplayName()).collect(Collectors.toList())));
            isOver = true;
        }
        if( isOver ){
            _log.info("Game " + _gameId + " over on turn " + _turnNumber +
                    " with " + _players.size() + " remaining players.");
        }
        return isOver;
    }

    public List<Country> findOccupiedCountries(Player player){
        return _occupations.countriesOccupied(player);
    }

    public boolean hasPossibleAttack(Player player){
        return findCountriesToAttackFrom(player).size() > 0;
    }

    public List<Country> findCountriesToAttackFrom(Player player){
        return ListUtils.filter(findBorderCountries(player), _occupations::hasAttackingForces);
    }

    public List<Country> findBorderCountries(Player player){
        return ListUtils.filter(findOccupiedCountries(player), _occupations::hasEnemyNeighbor);
    }

    public List<Country> findInteriorCountries(Player player){
        return ListUtils.filter(findOccupiedCountries(player), c -> ! _occupations.hasEnemyNeighbor(c));
    }

    private List<Continent> findContinentsOccupied(Player player){
        return ListUtils.filter(getAllContinents(), continent -> isContinentOwner(player, continent));
    }

    public List<Continent> findOwnedContinents(){
        return ListUtils.filter(getAllContinents(), this::isOwnedContinent);
    }

    public boolean isContinentOwner(Player player, Continent continent){
        return continent.getCountries().stream()
                .allMatch(country -> player.equals(getOccupier(country)));
    }

    public boolean isOwnedContinent(Continent continent){
        Player owner = getOccupier(continent.getCountries().get(0));
        return isContinentOwner(owner, continent);
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

    public boolean isContinentalBorder(Country country){ return _occupations.isContinentalBorder(country); }

    public List<Country> findContinentalBorders(Continent continent){
        return _occupations.findContinentalBorders(continent);
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

    public PlayerHoldings getPlayerHoldings(Player player){
        return _playerHoldings.get(player.getColor());
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

    private boolean isTarget(Country attacker, Country defender){
        if( _occupations.getMap().areNeighbors(attacker, defender)){
            return ! getOccupier(attacker).equals(getOccupier(defender));
        }
        return false;
    }

    private void publishState(Player[] players, Country[] countries){
        Arrays.asList(players).forEach(this::publishPlayerState);
        Arrays.asList(countries).forEach(this::publishCountryState);
    }

    public void publishAllState(){
        getAllCountries().forEach(this::publishCountryState);
        _players.forEach(this::publishPlayerState);
    }

    public void publishPlayerState(Player player){
        _channels.PlayerChangedEventChannel.publish(generatePlayerChangedEvent(player));
    }

    private PlayerChangedEvent generatePlayerChangedEvent(Player player){
        int countryCount = numberCountriesOccupied(player);
        int armyCount = _occupations.totalOccupationForces(player) + getPlayerHoldings(player).reserveCount();
        int continentCount = numberContinentsOccupied(player);
        int expectedGrant = computeExpectedGrant(player);
        PlayerHoldings playerHoldings = getPlayerHoldings(player);
        return new PlayerChangedEvent(player, playerHoldings.getCards(), countryCount, armyCount, continentCount, expectedGrant);
    }

    private void publishCountryState(Country country){
        int newCount = _occupations.getOccupationForce(country);
        Player player = _occupations.getOccupier(country);

        MapChangedEvent event = new MapChangedEvent(country, player, newCount);
        _channels.MapChangedEventChannel.publish(event);
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
