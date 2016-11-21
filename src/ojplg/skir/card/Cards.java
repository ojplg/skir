package ojplg.skir.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cards {

    public static boolean hasTradeableSet(List<Card> cards){
        CardSet set = findTradeableSet(cards);
        if (set == null){
            return false;
        }
        return set.isExchangeableSet();
    }

    public static CardSet findTradeableSet(List<Card> cards){
        if (cards.size() < 3){
            return null;
        }
        List<Card> copy = new ArrayList<Card>(cards);
        for (List<Card> subset : allSubsets(3, copy) ){
            CardSet set = new CardSet(subset.get(0), subset.get(1), subset.get(2));
            if( set.isExchangeableSet()){
                return set;
            }
        }
        return null;
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
}
