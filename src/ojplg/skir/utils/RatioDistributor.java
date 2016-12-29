package ojplg.skir.utils;

import java.util.HashMap;
import java.util.Map;

public class RatioDistributor {

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

    private static <T> Double computeDenominator(Map<T, Double> inputs){
        return inputs.values().stream().reduce(0.0, (a,b) -> a + b );
    }

}
