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
    public Order generateOrder(Adjutant adjutant, Game game) {
        List<OrderType> possibleOrderTypes = adjutant.allowableOrders();

        if( possibleOrderTypes.contains(OrderType.PlaceArmy)){
            return placeArmy(adjutant, game);
        }
        if( possibleOrderTypes.contains(OrderType.EndAttacks) ){
            return new EndAttacks(adjutant);
        }
        if( possibleOrderTypes.contains(OrderType.DrawCard) ){
            return new DrawCard(adjutant);
        }
        if( possibleOrderTypes.contains(OrderType.ClaimArmies)){
            return new ClaimArmies(adjutant);
        }
        if( possibleOrderTypes.contains(OrderType.Fortify)){
            return new EndTurn(adjutant);
        }
        _log.warn("Don't know what to do with these options " + possibleOrderTypes);
        return new EndTurn(adjutant);
    }


    public Player getPlayer(){
        return _me;
    }

    private PlaceArmy placeArmy(Adjutant adjutant, Game game){
        Country countryToFortify = AiUtils.findWeakestPossession(_me, game);
        return new PlaceArmy(adjutant,countryToFortify);
    }
}
