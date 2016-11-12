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
            if( game.hasLegalFortification(activePlayer())) {
                return getAdjutant().forOrderTypes(OrderType.DrawCard, OrderType.Fortify);
            } else {
                return getAdjutant().forOrderType(OrderType.DrawCard);
            }
        } else {
            if( game.hasLegalFortification(activePlayer())) {
                return getAdjutant().forOrderType(OrderType.Fortify);
            } else {
                Player nextPlayer = game.nextPlayer();
                return Adjutant.nextPlayer(nextPlayer);
            }
        }
    }

    @Override
    OrderType getType() {
        return OrderType.EndAttacks;
    }
}
