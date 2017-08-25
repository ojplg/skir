package ojplg.skir.ai;

import ojplg.skir.map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.ClaimArmies;
import ojplg.skir.play.orders.DrawCard;
import ojplg.skir.play.orders.EndAttacks;
import ojplg.skir.play.orders.EndTurn;
import ojplg.skir.play.orders.Order;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.play.orders.PlaceArmy;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

import java.util.List;

/**
 * This AI never attacks. It just place armies equitably
 * across the countries it owns.
 */
public class Wimpy implements AutomatedPlayer {

    private final static Logger _log = LogManager.getLogger(Wimpy.class);

    private final Player _me;

    public Wimpy(Player player){
        this(player, "Wimpy");
    }

    public Wimpy(Player player, String name){
        _me = player;
        player.setDisplayName(name);
    }

    @Override
    public void initialize(Game game) {
        // do nothing
    }

    private OrderType pickOrderType(List<OrderType> possibleOrderTypes) {
        if( possibleOrderTypes.contains(OrderType.PlaceArmy)){
            return OrderType.PlaceArmy;
        }
        if( possibleOrderTypes.contains(OrderType.EndAttacks) ){
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
        _log.warn("Don't know what to do with these options " + possibleOrderTypes);
        return null;
    }


    public Player getPlayer(){
        return _me;
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game){
        OrderType orderType = pickOrderType(adjutant.allowableOrders());

        Order order;
        if( orderType == OrderType.PlaceArmy){
            order = placeArmy(adjutant, game);
        } else if( orderType == OrderType.EndAttacks ){
            order = new EndAttacks(adjutant);
        } else if(orderType ==OrderType.DrawCard ){
            order = new DrawCard(adjutant);
        } else if( orderType == OrderType.ClaimArmies){
            order = new ClaimArmies(adjutant);
        } else if( orderType == OrderType.EndTurn){
            order = new EndTurn(adjutant);
        } else {
            _log.warn("Don't know what to do with this type " + orderType);
            return null;
        }
        return order;
    }

    private PlaceArmy placeArmy(Adjutant adjutant, Game game){
        Country countryToFortify = AiUtils.findWeakestPossession(_me, game);
        return new PlaceArmy(adjutant,countryToFortify);
    }
}
