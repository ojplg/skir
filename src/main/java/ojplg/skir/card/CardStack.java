package ojplg.skir.card;

import ojplg.skir.state.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardStack implements Serializable {

    private final List<Card> _cards = new ArrayList<>();
    private int _tradeNumber = 1;

    public CardStack(List<Card> cards){
        _cards.addAll(cards);
        Collections.shuffle(_cards);
    }

    public Card drawCard(){
        return _cards.remove(0);
    }

    public int tradeCards(CardSet set){
        if( ! set.isExchangeableSet()){
            throw new RuntimeException("Cannot exchange with " + set);
        }
        _cards.addAll(set.asList());
        int grantAmount = valuationOfExchange();
        _tradeNumber++;
        return grantAmount;
    }

    public int valuationOfExchange(){
        return Math.min(Constants.MAXIMUM_CARD_EXCHANGE, 2 * _tradeNumber);
    }
}
