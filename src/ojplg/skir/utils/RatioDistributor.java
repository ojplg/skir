package ojplg.skir.utils;

import java.util.HashMap;
import java.util.Map;

public class RatioDistributor {

    public static <T> Map<T,Integer> distribute(Map<T,Double> inputs, int amount){
        Double denominator = computeDenominator(inputs);
        Map<T,Integer> distributed = new HashMap<>();
        for(Map.Entry<T,Double> entry : inputs.entrySet()){
            int assignment = (int) Math.round((amount * (entry.getValue()/denominator)));
            if( assignment > 0){
                distributed.put(entry.getKey(), assignment);
            }
        }
        return distributed;
    }

    private static <T> Double computeDenominator(Map<T, Double> inputs){
        return inputs.values().stream().reduce(0.0, (a,b) -> a + b );
    }

}
