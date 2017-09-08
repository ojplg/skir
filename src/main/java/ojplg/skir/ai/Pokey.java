package ojplg.skir.ai;

import ojplg.skir.card.CardSet;
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
import ojplg.skir.state.Constants;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * An AI that implements a slow growth strategy.
 */
public class Pokey implements AutomatedPlayer {

    private final static Logger _log = LogManager.getLogger(Pokey.class);

    private final Player _me;
    private boolean _hasConqueredCountry = false;

    public Pokey(Player player){
        this(player, "Pokey");
    }

    public Pokey(Player player, String name){
        _me = player;
        player.setDisplayName(name);
    }

    @Override
    public void initialize(Game game) {
        // do nothing
    }

    private OrderType pickOrderType(List<OrderType> possibleOrderTypes, Game game) {
        if ( possibleOrderTypes.contains(OrderType.ExchangeCardSet)){
            return OrderType.ExchangeCardSet;
        }
        if( possibleOrderTypes.contains(OrderType.PlaceArmy)){
            return OrderType.PlaceArmy;
        }
        if( possibleOrderTypes.contains(OrderType.Attack) && shouldMakeAttack(game) ){
            return OrderType.Attack;
        } else if (possibleOrderTypes.contains(OrderType.Attack)){
            return OrderType.EndAttacks;
        }
        if( possibleOrderTypes.contains(OrderType.DrawCard) ){
            return OrderType.DrawCard;
        }
        if( possibleOrderTypes.contains(OrderType.ClaimArmies)){
            return OrderType.ClaimArmies;
        }
        if( possibleOrderTypes.contains(OrderType.Fortify)){
            return OrderType.EndTurn;
        }
        if ( possibleOrderTypes.contains(OrderType.Occupy)){
            return OrderType.Occupy;
        }
        if( possibleOrderTypes.contains(OrderType.EndTurn)){
            return OrderType.EndTurn;
        }
        throw new RuntimeException("Don't know what to do with these options " + possibleOrderTypes);
    }


    public Player getPlayer(){
        return _me;
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game){

        OrderType orderType = pickOrderType(adjutant.allowableOrders(), game);

        switch (orderType){
            case Attack:
                return findBestAttack(game, adjutant);
            case ClaimArmies:
                return new ClaimArmies(adjutant);
            case DrawCard:
                return new DrawCard(adjutant);
            case EndAttacks:
                return new EndAttacks(adjutant);
            case ExchangeCardSet:
                CardSet set = CardSet.findTradeableSet(game.getPlayerHoldings(getPlayer()).getCards());
                return new ExchangeCardSet(adjutant, set.getOne(), set.getTwo(), set.getThree());
            case Occupy:
                return generateOccupationOrder(adjutant, game);
            case PlaceArmy:
                return placeArmy(adjutant, game);
            case EndTurn:
                return new EndTurn(adjutant);
            default:
                _log.warn("Cannot handle order type " + orderType);
                return null;
        }
    }

    private PlaceArmy placeArmy(Adjutant adjutant, Game game){
        _hasConqueredCountry = false;
        int reserveCount = game.getPlayerHoldings(_me).reserveCount();
        List<Country> unorderedBorders = game.findBorderCountries(_me);
        List<Country> orderedBorders = AiUtils.orderByOccupationForce(unorderedBorders, game);
        for (int idx=0; idx<orderedBorders.size(); idx++){
            Country country = orderedBorders.get(idx);
            if( idx + 1 == orderedBorders.size()){
                return new PlaceArmy(adjutant, country, reserveCount);
            }
            int myForce = game.getOccupationForce(country);
            List<Country> enemyNeighbors = game.findEnemyNeighbors(country);
            for(Country neighbor : enemyNeighbors){
                int enemyForce = game.getOccupationForce(neighbor);
                if( enemyForce < myForce + reserveCount){
                    return new PlaceArmy(adjutant, country, reserveCount);
                }
            }
        }
        throw new RuntimeException("Something went wrong");
    }

    private Occupy generateOccupationOrder(Adjutant adjutant, Game game){
        _hasConqueredCountry = true;
        OccupationConstraints constraints = adjutant.getOccupationConstraints();
        int occupationForce = Math.max(constraints.minimumOccupationForce(),
                game.getOccupationForce(constraints.attacker())/2);
        return new Occupy(adjutant, constraints.attacker(), constraints.conquered(),occupationForce);
    }

    private Attack findBestAttack(Game game, Adjutant adjutant){
        PossibleAttack chosen = findBestPossibleAttack(game);
        int dice = Math.min(Constants.MAXIMUM_ATTACKER_DICE, game.getOccupationForce(chosen.getAttacker()) - 1);
        return new Attack(adjutant, chosen.getAttacker(), chosen.getDefender(),dice);
    }

    private boolean shouldMakeAttack(Game game){
        return findAdvantageousAttacks(game).size() > 0
                && ! _hasConqueredCountry;
    }

    private PossibleAttack findBestPossibleAttack(Game game){
        List<PossibleAttack> advantages = findAdvantageousAttacks(game);
        if( advantages.size() == 0 ){
            return null;
        }
        Collections.shuffle(advantages);
        Collections.sort(advantages);
        return advantages.get(0);
    }

    private List<PossibleAttack> findAdvantageousAttacks(Game game){
        return AiUtils.findAdvantageousAttacks(_me, game);
    }

}
