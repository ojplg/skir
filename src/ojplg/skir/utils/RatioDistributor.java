package ojplg.skir.utils;

import java.util.*;
import java.util.stream.Collectors;

public class RatioDistributor {

    public static <T> Map<T,Integer> distribute(Map<T,Double> inputs, int amount, int maximumOutput) {
        Map<T, Double> reducedInputs;
        if (maximumOutput < inputs.size()){
            reducedInputs = dropUnwantedInputs(inputs, maximumOutput);
        } else {
            reducedInputs = inputs;
        }
        return distribute(reducedInputs, amount);
    }

    public static <T> Map<T,Integer> distribute(Map<T,Double> inputs, int amount){

        Double denominator = computeDenominator(inputs);
        Map<T,Integer> distributed = new HashMap<>();
        int remainder = amount;
        Double largest = Double.MIN_VALUE;
        T largestItem = null;
        for(Map.Entry<T,Double> entry : inputs.entrySet()){
            if( remainder == 0){
                break;
            }
            if( largestItem == null || largest > entry.getValue() ){
                largestItem = entry.getKey();
                largest = entry.getValue();
            }
            int allotment = Math.min(remainder,(int) Math.round((amount * (entry.getValue()/denominator))));
            if( allotment > 0){
                remainder -= allotment;
                distributed.put(entry.getKey(), allotment);
            }
        }
        if (remainder > 0){
            if( distributed.containsKey(largestItem)) {
                int current = distributed.get(largestItem);
                distributed.put(largestItem, current + remainder);
            } else {
                distributed.put(largestItem, remainder);
            }
        }
        return distributed;
    }

    private static <T> Map<T, Double> dropUnwantedInputs(Map<T, Double> inputs, int maximumOutput){
        List<WeightedItem> itemList = inputs.entrySet().stream().
                map(e -> new WeightedItem(e.getKey(), e.getValue())).collect(Collectors.toList());
        Collections.sort(itemList);
        Collections.reverse(itemList);

        Map<T, Double> keptItems = new HashMap<T, Double>();
        for(WeightedItem<T> item : itemList.subList(0, maximumOutput)){
            keptItems.put(item.getItem(), item.getWeight());
        }

        return keptItems;
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
