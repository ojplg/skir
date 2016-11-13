package play.orders;

import card.Cards;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import state.Game;

public class ClaimArmies extends Order {

    private static final Logger _log = LogManager.getLogger(ClaimArmies.class);

    public ClaimArmies(Adjutant adjutant) {
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        _log.info("Claiming armies for " + getAdjutant().getActivePlayer());
        int numberToGrant = game.computeExpectedGrant(activePlayer());
        activePlayer().grantReserves(numberToGrant);
        game.publishPlayerChanged(activePlayer());

        _log.info("Claimed armies for " + getAdjutant().getActivePlayer());

        if(Cards.hasTradeableSet(activePlayer().getCards()) ){
            ConstrainedOrderType exchange = ConstrainedOrderType.unconstrainedOrder(OrderType.ExchangeCardSet);
            ConstrainedOrderType placeArmy = ConstrainedOrderType.placeArmy(activePlayer(),game);
            return getAdjutant().forConstrainedOrderTypes(exchange, placeArmy);
        } else {
            return getAdjutant().forConstrainedOrderTypes(ConstrainedOrderType.placeArmy(
                    activePlayer(),game));
        }
    }

    @Override
    public OrderType getType() {
        return OrderType.ClaimArmies;
    }
}

