package ojplg.skir.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
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

    public static <T extends Comparable<T>> Optional<T> findMax(List<T> ts, Function<T, Boolean> aboveMinimum){
        Optional<T> best = findMax(ts);
        if( best.isPresent() && aboveMinimum.apply(best.get()) ){
            return best;
        }
        return Optional.empty();
    }

    public static <T> double sumAll(List<T> list, Function<T, Double> valuer){
        return list.stream().reduce(
                0d,
                (s, c) -> s + valuer.apply(c),
                (s1, s2) -> s1 + s2);
    }

    public static <T, V> Map<T,V> mapify(List<T> list, Function<T, V> mapper){
        Map<T,V> map = new HashMap();
        list.forEach(t -> map.put(t, mapper.apply(t)));
        return map;
    }
}
