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
        _me = player;
        player.setDisplayName("Wimpy");
    }

    private OrderType pickOrderType(List<OrderType> possibleOrderTypes, Game game) {
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
        OrderType orderType = pickOrderType(adjutant.allowableOrders(), game);

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
        List<Country> countries = game.countriesOccupied(_me);
        int minArmies = game.getOccupationForce(countries.get(0));
        Country countryToFortify = countries.get(0);
        for(Country country : countries){
            if( game.getOccupationForce(country) < minArmies){
                minArmies = game.getOccupationForce(country);
                countryToFortify = country;
            }
        }
        return new PlaceArmy(adjutant,countryToFortify);
    }
}
