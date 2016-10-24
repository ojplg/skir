package play.orders;

import map.Country;
import state.Game;
import state.Player;

public class PlaceArmy extends Order {

    private final Country _country;
    private final int _count;

    public PlaceArmy(Adjutant adjutant, Country country){
        super(adjutant);
        _country = country;
        _count = 1;
    }

    public PlaceArmy(Adjutant adjutant, Country country, int count){
        super(adjutant);
        _country = country;
        _count = count;
    }


    @Override
    public Adjutant execute(Game game) {
        // TODO: is this the wrong thing to check? What about when a country is newly occupied?
        if( activePlayer() != game.getOccupier(_country)){
            throw new RuntimeException("Player " + activePlayer() + " cannot place armies in " + _country);
        }
        game.placeArmies(activePlayer(), _country, _count);
        if ( activePlayer().hasReserves() ){
            getAdjutant().setAllowableOrders(OrderType.PlaceArmy);
        } else{
            getAdjutant().setAllowableOrders(OrderType.Attack, OrderType.EndAttacks);
        }
        return getAdjutant();
    }

    @Override
    OrderType getType() {
        return OrderType.PlaceArmy;
    }
}
