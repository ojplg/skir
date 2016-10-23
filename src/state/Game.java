package state;

import card.Card;
import card.CardStack;
import map.Continent;
import map.Country;
import map.WorldMap;
import play.Rolls;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class Game {

    private WorldMap _map;
    private Occupations _occupations = new Occupations();
    private List<Player> _players = new ArrayList<Player>();
    private CardStack _cardPile;

    public Game(WorldMap map, List<Player> players, List<Card> cards){
        _map = map;
        _players.addAll(players);
        _cardPile = new CardStack(cards);
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
        return Math.min(3, numberCountriesOccupied(player) / 3);
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
        List<Country> targets = new ArrayList<Country>();
        Player occupier = getOccupier(country);
        for (Country neighbor : _map.getNeighbors(country)){
            if( ! occupier.equals(getOccupier(neighbor)) ) {
                targets.add(neighbor);
            }
        }
        return targets;
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
        player.drawReserves(1);
        _occupations.placeArmy(player, country);
    }

    public void fortify(Country source, Country destination, int armies){
        // TODO: check for constraints
        Player player = getOccupier(source);
        _occupations.killArmies(source, armies);
        _occupations.placeArmies(player, destination, armies);
    }

    public Card drawCard() {
        return _cardPile.drawCard();
    }

    public int tradeCards(Card one, Card two, Card three){
        return _cardPile.tradeCards(one, two, three);
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
