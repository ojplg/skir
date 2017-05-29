package ojplg.skir.evolve;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Zygote {

    private static final Random _random = new Random();

    private final Map<String,Double> _genes;

    public Zygote(Map<String,Double> leftGamete, Map<String,Double> rightGamete){
        _genes = fertilize(leftGamete, rightGamete);
    }

    public Map<String,Double> getGenes(){
        return _genes;
    }

    private Map<String,Double> fertilize(Map<String,Double> leftGamete, Map<String,Double> rightGamete){
        Map<String,Double> genes = new HashMap<>();
        for(String gene : leftGamete.keySet()){
            Double left = leftGamete.get(gene);
            Double right = rightGamete.get(gene);
            genes.put(gene, join(left, right));
        }
        return genes;
    }

    private Double join(Double left, Double right){
        float chance = _random.nextFloat();
        if( chance < 0.3 ){
            return left;
        } else if ( chance < 0.6 ){
            return right;
        } else if ( chance < 0.9 ) {
            return (left + right) / 2;
        } else if ( chance < 0.95 ) {
            return Math.min(left, right) / 2;
        } else {
            return (1 + Math.max(left, right)) / 2;
        }
    }
}
