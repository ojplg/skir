package ojplg.skir.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cards {

    public static boolean hasTradeableSet(List<Card> cards){
        return findTradeableSet(cards).size() > 0;
    }

    public static List<Card> findTradeableSet(List<Card> cards){
        if (cards.size() < 3){
            return Collections.EMPTY_LIST;
        }
        List<Card> copy = new ArrayList<Card>(cards);
        for (List<Card> subset : allSubsets(3, copy) ){
            if(canTrade(subset.get(0), subset.get(1), subset.get(2))){
                return subset;
            }
        }
        return Collections.EMPTY_LIST;
    }

    public static <T> List<List<T>> allSubsets(int length, List<T> items){
        if (length > items.size()) {
            return Collections.EMPTY_LIST;
        }
        if (length == 0 ){
            return Collections.EMPTY_LIST;
        }
        List<List<T>> sublists = new ArrayList<List<T>>();
        if( length == 1 ){
            for( T item : items ){
                List<T> singleton = new ArrayList<T>();
                singleton.add(item);
                sublists.add(singleton);
            }
            return sublists;
        }
        List<T> copy = new ArrayList<T>(items);
        T item = copy.remove(0);

        List<List<T>> subs = allSubsets(length - 1, copy);
        for (List<T> sub : subs){
            sub.add(item);
            sublists.add(sub);
        }
        sublists.addAll(allSubsets(length, copy));

        return sublists;
    }

    public static boolean canTrade(Card one, Card two, Card three){
        if( one.matchesType(two) && one.matchesType(three) ){
           return true;
        }
        if ( one.unMatchesType(two) && one.unMatchesType(three) && two.unMatchesType(three) ){
            return true;
        }
        return false;
    }
}