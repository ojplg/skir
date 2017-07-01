package ojplg.skir.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListUtils {
    public static <T> List<T> filter(List<T> items, Predicate<? super T> predicate){
        return items.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T> List<T> concat(List<T> list1, List<T> list2){
        List<T> concatenatedList = new ArrayList<>();
        concatenatedList.addAll(list1);
        concatenatedList.addAll(list2);
        return concatenatedList;
    }

    public static <T> boolean hasIntersection(List<T> as, List<T> bs){
        for(T a : as){
            if( bs.contains(a)){
                return true;
            }
        }
        return false;
    }

    public static <T extends Comparable<T>> Optional<T> findMax(List<T> ts){
        Collections.shuffle(ts);
        return ts.stream().max(Comparator.naturalOrder());
    }
}
