package ojplg.skir.ai;

import ojplg.skir.card.CardSet;
import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Attack;
import ojplg.skir.play.orders.ClaimArmies;
import ojplg.skir.play.orders.DrawCard;
import ojplg.skir.play.orders.EndAttacks;
import ojplg.skir.play.orders.EndTurn;
import ojplg.skir.play.orders.ExchangeCardSet;
import ojplg.skir.play.orders.Fortify;
import ojplg.skir.play.orders.OccupationConstraints;
import ojplg.skir.play.orders.Occupy;
import ojplg.skir.play.orders.Order;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.play.orders.PlaceArmy;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This AI concentrates on trying to win continents
 */
public class Grabby implements AutomatedPlayer {

    private static final Logger _log = LogManager.getLogger(Grabby.class);

    private final Player _me;

    public Grabby(Player player){
        this._me = player;
        player.setDisplayName("Grabby");
    }

    @Override
    public Object getIdentification() {
        return "AI: Grabby";
    }


    @Override
    public void initialize(Game game) {
        // do nothing
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {
        List<OrderType> possibleOrderTypes = adjutant.allowableOrders();
        _log.debug("Selecting from order types " + possibleOrderTypes);
        if(possibleOrderTypes.size() == 1){
            OrderType orderType = possibleOrderTypes.get(0);
            if( orderType == OrderType.PlaceArmy){
                return placeArmies(adjutant, game);
            }
            if (orderType == OrderType.Occupy){
                return occupy(adjutant, game);
            }
            return dealWithNoChoiceOrderTypes(adjutant,orderType);
        }
        if( possibleOrderTypes.contains(OrderType.ExchangeCardSet)){
            return new ExchangeCardSet(adjutant, CardSet.findTradeableSet(_me.getCards()));
        }
        if( possibleOrderTypes.contains(OrderType.Attack)){
            return possiblyAttack(adjutant, game);
        }
        if( possibleOrderTypes.contains(OrderType.Fortify)){
            Fortify fortification = possiblyFortify(adjutant, game);
            if( fortification != null){
                _log.debug("Fortifying! from " + fortification.getSource() + " to " + fortification.getDestination());
                return fortification;
            }
        }
        if( possibleOrderTypes.contains(OrderType.DrawCard)){
            return new DrawCard(adjutant);
        }
        return new EndTurn(adjutant);
    }

    private Fortify possiblyFortify(Adjutant adjutant, Game game){
        List<Country> mine = game.findInteriorCountries(_me);
        _log.debug("Searching for fortifications from " + mine.size() + " countries");
        int numberToFortify = 0;
        Country from = null;
        Country to = null;
        for(Country country : mine){
            int possibleNumber = game.getOccupationForce(country) - 1;
            if( possibleNumber > numberToFortify){
                List<Country> neighbors = game.findAllNeighbors(country);
                _log.debug("Fortifying from " + country + " has " + neighbors.size() + " options");
                for(Country neighbor : neighbors){
                    if(game.findEnemyNeighbors(neighbor).size() > 0){
                        numberToFortify = possibleNumber;
                        from = country;
                        to = neighbor;
                    }
                }
            }
        }
        if( numberToFortify > 0 ){
            return new Fortify(adjutant, from, to, numberToFortify);
        }
        return null;
    }

    private Order possiblyAttack(Adjutant adjutant, Game game){
        PossibleAttack possibleAttack = findPossibleAttack(game);
        if( possibleAttack == null) {
            _log.debug("ending attacks");
            return new EndAttacks(adjutant);
        } else {
            _log.debug(_me + " attacking ! from  " + possibleAttack.getAttacker() + " to  " + possibleAttack.getDefender());
            return new Attack(adjutant, possibleAttack.getAttacker(),
                    possibleAttack.getDefender(), AiUtils.attackingDice(game, possibleAttack.getAttacker()));
        }
    }

    private Order occupy(Adjutant adjutant, Game game){
        OccupationConstraints constraints = adjutant.getOccupationConstraints();
        Country from = constraints.attacker();
        int occupationForce;
        if( game.findEnemyNeighbors(from).size() == 1 ){
            occupationForce = game.getOccupationForce(from) - 1;
        } else {
            occupationForce = Math.max(constraints.minimumOccupationForce(),
                    game.getOccupationForce(from)/2);
        }
        return new Occupy(adjutant, from, constraints.conquered(), occupationForce);
    }

    private PossibleAttack findPossibleAttack(Game game){
        _log.debug("Choosing attacks for " + _me);
        Continent continent = AiUtils.findStrongestUnownedContinent(_me, game);
        if( continent == null ){
            _log.debug("need to find an off-continent attack");
            return findIntercontinentalAttack(game);
        }
        _log.debug("Choosing attacks on " + continent);
        List<PossibleAttack> possibleAttacks = AiUtils.findAdvantageousAttacks(_me, game);
        _log.debug("Possible attack count " + possibleAttacks.size());
        List<PossibleAttack> possibleContinentAttacks = possibleAttacks.stream()
                                .filter( pa -> { return continent.getCountries().contains(pa.getDefender());})
                                .collect(Collectors.toList());
        return RandomUtils.pickRandomElement(possibleContinentAttacks);
    }

    private PossibleAttack findIntercontinentalAttack(Game game){
        List<PossibleAttack> possibleAttacks = AiUtils.findAdvantageousAttacks(_me, game);
        return RandomUtils.pickRandomElement(possibleAttacks);
    }

    private Order placeArmies(Adjutant adjutant, Game game){
        _log.debug("Choosing placement for " + _me);
        Continent continent = AiUtils.findStrongestUnownedContinent(_me, game);
        _log.debug("Planning to place in " + continent);
        if( continent == null ){
            List<Country> countries = game.findOccupiedCountries(_me);
            List<Country> borderCountries = countries.stream()
                    .filter(c -> game.findEnemyNeighbors(c).size() > 0)
                    .collect(Collectors.toList());
            Country country = RandomUtils.pickRandomElement(borderCountries);
            _log.debug("Going to place in (after continent null) " + country);
            return new PlaceArmy(adjutant, country);
        }
        List<Country> myCountries = continent.getCountries().stream()
                .filter(c -> { return game.getOccupier(c).equals(_me);})
                .filter(c -> { return game.findEnemyNeighbors(c).size() > 0; })
                .collect(Collectors.toList());
        Country country = RandomUtils.pickRandomElement(myCountries);
        _log.debug("Going to place in " + country);
        return new PlaceArmy(adjutant,country);
    }

    private Order dealWithNoChoiceOrderTypes(Adjutant adjutant,OrderType orderType){
        switch(orderType){
            case ExchangeCardSet:
                return new ExchangeCardSet(adjutant, CardSet.findTradeableSet(_me.getCards()));
            case ClaimArmies:
                return new ClaimArmies(adjutant);
            case DrawCard:
                return new DrawCard(adjutant);
            case EndTurn:
                return new EndTurn(adjutant);
            default:
                throw new RuntimeException("No way to deal with order type " + orderType);
        }
    }

    @Override
    public Player getPlayer() {
        return _me;
    }
}
