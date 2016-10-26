package ai;

import map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import play.orders.*;
import state.Game;
import state.Player;

import java.util.List;

public class NeverAttacks implements AutomatedPlayer {

    private final static Logger _log = LogManager.getLogger(NeverAttacks.class);

    private final Player _me;

    public NeverAttacks(Player player){
        _me = player;
    }

    @Override
    public OrderType pickOrder(List<OrderType> possibleOrderTypes, Game game) {
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
        System.out.println("Don't know what to do with these options " + possibleOrderTypes);
        return null;
    }


    public Player getPlayer(){
        return _me;
    }

    @Override
    public Adjutant executeOrder(OrderType orderType, Adjutant adjutant, Game game){
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
        return order.execute(game);
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
