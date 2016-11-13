package play.orders;

import state.Game;
import state.Player;

public class EndAttacks extends Order {

    public EndAttacks(Adjutant adjutant){
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        ConstrainedOrderType fortification = ConstrainedOrderType.fortify(activePlayer(), game);
        ConstrainedOrderType drawCard = ConstrainedOrderType.unconstrainedOrder(OrderType.DrawCard);
        ConstrainedOrderType endTurn = ConstrainedOrderType.unconstrainedOrder(OrderType.EndTurn);
        if( getAdjutant().hasConqueredCountry() && ! activePlayer().hasMaximumCards() ) {

            if( game.hasLegalFortification(activePlayer())) {

                return getAdjutant().forConstrainedOrderTypes(drawCard, fortification);
            } else {
                return getAdjutant().forConstrainedOrderTypes(drawCard);
            }
        } else {
            if( game.hasLegalFortification(activePlayer())) {
                return getAdjutant().forConstrainedOrderTypes(fortification, endTurn);
            } else {
                Player nextPlayer = game.nextPlayer();
                return Adjutant.nextPlayer(nextPlayer);
            }
        }
    }

    @Override
    public OrderType getType() {
        return OrderType.EndAttacks;
    }
}
