package ojplg.skir.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RatioDistributor {

    public static <T> Map<T,Integer> distribute(Map<T,Double> inputs, int amount, List<Integer> minimums){
        Double denominator = computeDenominator(inputs);
        List<WeightedItem<T>> orderedItems = itemsInOrder(inputs);
        Map<T,Integer> distributed = new HashMap<>();
        int remainder = amount;
        int index = 0;
        for(WeightedItem<T> entry : orderedItems){
            if( remainder == 0){
                break;
            }
            int preferredAllotment = (int) Math.round((amount * (entry.getWeight()/denominator)));
            if(minimums.size() > index){
                preferredAllotment = Math.max(minimums.get(index), preferredAllotment);
            }
            int allotment = Math.min(preferredAllotment, remainder);
            if( allotment > 0){
                remainder -= allotment;
                distributed.put(entry.getItem(), allotment);
            }
            index++;
        }
        if (remainder > 0){
            T largestItem = orderedItems.get(0).getItem();
            if( distributed.containsKey(largestItem)) {
                int current = distributed.get(largestItem);
                distributed.put(largestItem, current + remainder);
            } else {
                distributed.put(largestItem, remainder);
            }
        }
        return distributed;
    }


    public static <T> Map<T,Integer> distribute(Map<T,Double> inputs, int amount, int maximumItems) {
        Map<T, Double> reducedInputs;
        if (maximumItems < inputs.size()){
            reducedInputs = dropUnwantedInputs(inputs, maximumItems);
        } else {
            reducedInputs = inputs;
        }
        return distribute(reducedInputs, amount);
    }

    public static <T> Map<T,Integer> distribute(Map<T,Double> inputs, int amount){
        return distribute(inputs, amount, Collections.emptyList());
    }

    private static <T> Map<T, Double> dropUnwantedInputs(Map<T, Double> inputs, int maximumOutput){
        List<WeightedItem<T>> itemList = itemsInOrder(inputs);

        Map<T, Double> keptItems = new HashMap<>();
        for(WeightedItem<T> item : itemList.subList(0, maximumOutput)){
            keptItems.put(item.getItem(), item.getWeight());
        }

        return keptItems;
    }

    private static <T> List<WeightedItem<T>> itemsInOrder(Map<T, Double> inputs){
        List<WeightedItem<T>> itemList = inputs.entrySet().stream().
                map(e -> new WeightedItem<>(e.getKey(), e.getValue())).collect(Collectors.toList());
        Collections.sort(itemList);
        Collections.reverse(itemList);
        return itemList;
    }

    private static <T> Double computeDenominator(Map<T, Double> inputs){
        return inputs.values().stream().reduce(0.0, (a,b) -> a + b );
    }

    private static class WeightedItem<T> implements Comparable<WeightedItem> {

        private final T _item;
        private final Double _weight;

        WeightedItem(T item, Double weight){
            _item = item;
            _weight = weight;
        }

        T getItem(){
            return _item;
        }

        Double getWeight(){
            return _weight;
        }

        @Override
        public int compareTo(WeightedItem o) {
            return this._weight.compareTo(o._weight);
        }
    }

}
