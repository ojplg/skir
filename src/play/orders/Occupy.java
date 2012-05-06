package play.orders;

import map.Country;
import state.Game;
import state.Player;

public class Occupy extends Order {

    private Country _victor;
    private Country _conquered;
    private int _armies;

    public Occupy(Adjutant adjutant, Country victor, Country conquered, int armies) {
        super(adjutant);
        _victor = victor;
        _conquered = conquered;
        _armies = armies;
    }

    @Override
    Adjutant execute(Game game) {
        Player loser = game.getOccupier(_conquered);
        if ( game.resolveConquest(_victor, _conquered, _armies) ){
            // TODO: Check for game over?
            game.resolveElimination(activePlayer(), loser);
            if (activePlayer().hasTooManyCards()){
                getAdjutant().setAllowableOrders(OrderType.ExchangeCardSet);
            } else {
                getAdjutant().setAllowableOrders(OrderType.Attack, OrderType.EndAttacks);
            }
        } else {
            getAdjutant().setAllowableOrders(OrderType.Attack, OrderType.EndAttacks);
        }
        return getAdjutant();
    }

    @Override
    OrderType getType() {
        return OrderType.Occupy;
    }
}
