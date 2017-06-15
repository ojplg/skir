package ojplg.skir.play.orders;

import ojplg.skir.card.CardSet;
import ojplg.skir.state.PlayerHoldings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ojplg.skir.state.Game;

public class ClaimArmies extends Order {

    private static final Logger _log = LogManager.getLogger(ClaimArmies.class);

    public ClaimArmies(Adjutant adjutant) {
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        _log.debug("Claiming armies for " + getAdjutant().getActivePlayer());
        int numberToGrant = game.computeExpectedGrant(activePlayer());
        PlayerHoldings activeHoldings = game.getPlayerHoldings(activePlayer());

        activeHoldings.grantReserves(numberToGrant);
        game.publishPlayerState(activePlayer());

        _log.debug("Claimed armies for " + getAdjutant().getActivePlayer());

        if(CardSet.hasTradeableSet(activeHoldings.getCards()) ){
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

