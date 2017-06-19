package ojplg.skir.play.orders;

import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

public class EndAttacks extends Order {

    public EndAttacks(Adjutant adjutant){
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        ConstrainedOrderType fortification = ConstrainedOrderType.fortify(activePlayer(), game);
        ConstrainedOrderType drawCard = ConstrainedOrderType.unconstrainedOrder(OrderType.DrawCard);
        ConstrainedOrderType endTurn = ConstrainedOrderType.unconstrainedOrder(OrderType.EndTurn);

        if( getAdjutant().hasConqueredCountry() && ! game.getPlayerHoldings(activePlayer()).hasMaximumCards() ) {

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
                return getAdjutant().nextPlayer(nextPlayer, game.getTurnNumber());
            }
        }
    }

    @Override
    public OrderType getType() {
        return OrderType.EndAttacks;
    }
}
