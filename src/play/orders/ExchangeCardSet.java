package play.orders;

import card.Card;
import card.Cards;
import state.Game;
import state.Player;

public class ExchangeCardSet extends Order {

    Card _one;
    Card _two;
    Card _three;

    public ExchangeCardSet(Adjutant adjutant, Card one, Card two, Card three){
        super(adjutant);
        _one = one;
        _two = two;
        _three = three;
    }

    public Adjutant execute(Game game){

        if( ! Cards.canTrade(_one, _two, _three) ) {
            throw new RuntimeException("Not a good set: " + _one + "," + _two + "," + _three);
        }

        int armies = game.tradeCards(_one, _two, _three);
        activePlayer().grantReserves(armies);

        return getAdjutant().forOrderType(OrderType.PlaceArmy);
    }

    @Override
    OrderType getType() {
        return OrderType.ExchangeCardSet;
    }
}
