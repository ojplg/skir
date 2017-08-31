package ojplg.skir.play.orders;

import ojplg.skir.card.Card;
import ojplg.skir.card.CardSet;
import ojplg.skir.state.Game;
import ojplg.skir.state.GameException;
import ojplg.skir.state.PlayerHoldings;

public class ExchangeCardSet extends Order {

    private final Card _one;
    private final Card _two;
    private final Card _three;

    public ExchangeCardSet(Adjutant adjutant, CardSet cardSet){
        super(adjutant);
        _one = cardSet.getOne();
        _two = cardSet.getTwo();
        _three = cardSet.getThree();
    }

    public ExchangeCardSet(Adjutant adjutant, Card one, Card two, Card three){
        super(adjutant);
        _one = one;
        _two = two;
        _three = three;
    }

    public ExchangeCardSet(Adjutant adjutant){
        super(adjutant);
        _one = null;
        _two = null;
        _three = null;
    }

    public Adjutant execute(Game game){
        CardSet set;

        if( _one == null){
            set = CardSet.findTradeableSet(game.getPlayerHoldings(getAdjutant().getActivePlayer()).getCards());
        } else {
            set = new CardSet(_one, _two, _three);
        }

        if( ! set.isExchangeableSet() ) {
            throw new GameException(getGameId(), "Not a good set: " + _one + "," + _two + "," + _three);
        }

        game.processExchangeCardSetOrder(set);

        PlayerHoldings activeHoldings = game.getPlayerHoldings(activePlayer());

        if( activeHoldings.hasTooManyCards()){
            return getAdjutant().forConstrainedOrderTypes(ConstrainedOrderType.unconstrainedOrder(OrderType.ExchangeCardSet));
        }

        if( CardSet.hasTradeableSet(activeHoldings.getCards())){
            getAdjutant().forConstrainedOrderTypes(
                    ConstrainedOrderType.placeArmy(activePlayer(), game),
                    ConstrainedOrderType.unconstrainedOrder(OrderType.ExchangeCardSet));
        }

        return getAdjutant().forConstrainedOrderTypes(ConstrainedOrderType.placeArmy(activePlayer(), game));
    }

    @Override
    public OrderType getType() {
        return OrderType.ExchangeCardSet;
    }
}
