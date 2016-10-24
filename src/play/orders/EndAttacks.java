package play.orders;

import state.Game;
import state.Player;

public class EndAttacks extends Order {

    public EndAttacks(Adjutant adjutant){
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        if( getAdjutant().hasConqueredCountry() && ! activePlayer().hasMaximumCards() ) {
            getAdjutant().setAllowableOrders(OrderType.DrawCard);
        } else {
            getAdjutant().setAllowableOrders(OrderType.Fortify);
        }
        return getAdjutant();
    }

    @Override
    OrderType getType() {
        return OrderType.EndAttacks;
    }
}
