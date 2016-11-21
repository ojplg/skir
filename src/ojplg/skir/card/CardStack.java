package ojplg.skir.card;

import ojplg.skir.state.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardStack {

    private final List<Card> _cards = new ArrayList<Card>();
    private int _tradeNumber = 0;

    public CardStack(List<Card> cards){
        _cards.addAll(cards);
        Collections.shuffle(_cards);
    }

    public Card drawCard(){
        return _cards.remove(0);
    }

    public int tradeCards(Card one, Card two, Card three){
        CardSet set = new CardSet(one, two, three);
        if( ! set.isExchangeableSet()){
            throw new RuntimeException("Cannot exchange with " + one + ", " + two + ", " + three);
        }
        _tradeNumber++;
        _cards.add(one);
        _cards.add(two);
        _cards.add(three);
        return valuationOfExchange();
    }

    private int valuationOfExchange(){
        return Math.min(Constants.MAXIMUM_CARD_EXCHANGE, 2 * _tradeNumber);
    }
}
