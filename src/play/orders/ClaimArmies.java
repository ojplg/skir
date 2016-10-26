package play.orders;

import card.Cards;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import state.Game;
import state.Player;

public class ClaimArmies extends Order {

    private static final Logger _log = LogManager.getLogger(ClaimArmies.class);

    private static final int MINIMUM = 3;

    public ClaimArmies(Adjutant adjutant) {
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        _log.info("Claiming armies for " + getAdjutant().getActivePlayer());
        int geographicArmies = game.computeMapSupply(activePlayer());
        int numberToGrant = Math.max(MINIMUM, geographicArmies);
        activePlayer().grantReserves(numberToGrant);

        if(Cards.hasTradeableSet(activePlayer().getCards()) ){
            getAdjutant().setAllowableOrders(OrderType.ExchangeCardSet, OrderType.PlaceArmy);
        } else {
            getAdjutant().setAllowableOrders(OrderType.PlaceArmy);
        }
        _log.info("Claimed armies for " + getAdjutant().getActivePlayer());
        return getAdjutant();
    }

    @Override
    OrderType getType() {
        return OrderType.ClaimArmies;
    }
}

