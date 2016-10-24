package play.orders;

import card.Cards;
import state.Game;
import state.Player;

public class ClaimArmies extends Order {

    private static final int MINIMUM = 3;

    public ClaimArmies(Adjutant adjutant) {
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        int geographicArmies = game.computeMapSupply(activePlayer());
        int numberToGrant = Math.max(MINIMUM, geographicArmies);
        activePlayer().grantReserves(numberToGrant);

        if(Cards.hasTradeableSet(activePlayer().getCards()) ){
            getAdjutant().setAllowableOrders(OrderType.ExchangeCardSet, OrderType.PlaceArmy);
        } else {
            getAdjutant().setAllowableOrders(OrderType.PlaceArmy);
        }
        return getAdjutant();
    }

    @Override
    OrderType getType() {
        return OrderType.ClaimArmies;
    }
}

