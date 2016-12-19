package ojplg.skir.card;

import java.util.ArrayList;
import java.util.List;

public class CardSet {
    private final Card _one;
    private final Card _two;
    private final Card _three;

    public CardSet(Card one, Card two, Card three){
        _one = one;
        _two = two;
        _three = three;
    }

    public static boolean hasTradeableSet(List<Card> cards){
        return findTradeableSet(cards) != null;
    }

    public static CardSet findTradeableSet(List<Card> cards){
        if (cards.size() < 3){
            return null;
        }
        List<Card> copy = new ArrayList<Card>(cards);
        for (List<Card> subset : Subsets.allSubsets(3, copy) ){
            CardSet set = new CardSet(subset.get(0), subset.get(1), subset.get(2));
            if( set.isExchangeableSet()){
                return set;
            }
        }
        return null;
    }

    public Card getOne() {
        return _one;
    }

    public Card getTwo() {
        return _two;
    }

    public Card getThree() {
        return _three;
    }

    public List<Card> asList(){
        List<Card> list = new ArrayList<>();
        list.add(_one);
        list.add(_two);
        list.add(_three);
        return list;
    }

    public boolean isExchangeableSet(){
        if (_one == null || _two == null || _three == null){
            return false;
        }
        if( _one.matchesType(_two) && _one.matchesType(_three) ){
            return true;
        }
        if ( _one.unMatchesType(_two) && _one.unMatchesType(_three) && _two.unMatchesType(_three) ){
            return true;
        }
        return false;
    }
}
