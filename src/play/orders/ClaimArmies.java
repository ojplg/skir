package play.orders;

import card.Cards;
import state.Game;
import state.Player;

public class ClaimArmies extends Order {

    public ClaimArmies(Adjutant adjutant) {
        super(adjutant);
    }

    @Override
    Adjutant execute(Game game) {
        int geographicArmies = game.computeMapSupply(activePlayer());
        activePlayer().grantReserves(geographicArmies);

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

