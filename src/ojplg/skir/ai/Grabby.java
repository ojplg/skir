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

public class Grabby implements AutomatedPlayer {

    private static final Logger _log = LogManager.getLogger(Grabby.class);

    private final Player _me;

    public Grabby(Player player){
        this._me = player;
        player.setDisplayName("AI: Grabby");
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {
        List<OrderType> possibleOrderTypes = adjutant.allowableOrders();
        _log.info("Selecting from order types " + possibleOrderTypes);
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
        if( possibleOrderTypes.contains(OrderType.DrawCard)){
            return new DrawCard(adjutant);
        }
        return new EndTurn(adjutant);
    }

    private Order possiblyAttack(Adjutant adjutant, Game game){
        PossibleAttack possibleAttack = findPossibleAttack(game);
        if( possibleAttack == null) {
            _log.info("ending attacks");
            return new EndAttacks(adjutant);
        } else {
            _log.info(_me + " attacking ! from  " + possibleAttack.getAttacker() + " to  " + possibleAttack.getDefender());
            return new Attack(adjutant, possibleAttack.getAttacker(),
                    possibleAttack.getDefender(), AiUtils.attackingDice(game, possibleAttack.getAttacker()));
        }
    }

    private Order occupy(Adjutant adjutant, Game game){
        OccupationConstraints constraints = adjutant.getOccupationConstraints();
        int occupationForce = Math.max(constraints.minimumOccupationForce(),
                game.getOccupationForce(constraints.attacker())/2);
        return new Occupy(adjutant, constraints.attacker(), constraints.conquered(),occupationForce);
    }

    private PossibleAttack findPossibleAttack(Game game){
        _log.info("Choosing attacks for " + _me);
        Continent continent = AiUtils.findStrongestUnownedContinent(_me, game);
        if( continent == null ){
            _log.warn("need to find an off-continent attack");
            return null;
        }
        _log.info("Choosing attacks on " + continent);
        List<PossibleAttack> possibleAttacks = AiUtils.findAdvantageousAttacks(_me, game);
        _log.info("Possible attack count " + possibleAttacks.size());
        List<PossibleAttack> possibleContinentAttacks = possibleAttacks.stream()
                                .filter( pa -> { return continent.getCountries().contains(pa.getDefender());})
                                .collect(Collectors.toList());
        return RandomUtils.pickRandomElement(possibleContinentAttacks);
    }

    private Order placeArmies(Adjutant adjutant, Game game){
        _log.info("Choosing placement for " + _me);
        Continent continent = AiUtils.findStrongestUnownedContinent(_me, game);
        _log.info("Planning to place in " + continent);
        if( continent == null ){
            Country country = RandomUtils.pickRandomElement(game.countriesOccupied(_me));
            _log.info("Going to place in " + country);
            return new PlaceArmy(adjutant, country);
        }
        List<Country> myCountries = continent.getCountries().stream()
                .filter(c -> { return game.getOccupier(c).equals(_me);})
                .collect(Collectors.toList());
        Country country = RandomUtils.pickRandomElement(myCountries);
        _log.info("Going to place in " + country);
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
