package ojplg.skir.play.orders;

import ojplg.skir.card.Card;
import ojplg.skir.card.CardSet;
import ojplg.skir.card.Cards;
import ojplg.skir.state.Game;

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
        CardSet set = new CardSet(_one, _two, _three);

        if( ! set.isExchangeableSet() ) {
            throw new RuntimeException("Not a good set: " + _one + "," + _two + "," + _three);
        }

        int armies = game.tradeCards(_one, _two, _three);
        activePlayer().grantReserves(armies);

        return getAdjutant().forConstrainedOrderTypes(ConstrainedOrderType.placeArmy(activePlayer(), game));
    }

    @Override
    public OrderType getType() {
        return OrderType.ExchangeCardSet;
    }
}
