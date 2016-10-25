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
            if( game.hasLegalFortification(activePlayer())) {
                getAdjutant().setAllowableOrders(OrderType.Fortify);
            } else {
                return new Adjutant(game.nextPlayer(), game.getRoller());
            }
        }
        return getAdjutant();
    }

    @Override
    OrderType getType() {
        return OrderType.EndAttacks;
    }
}
