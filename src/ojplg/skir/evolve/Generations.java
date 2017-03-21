package ojplg.skir.evolve;

import java.util.ArrayList;
import java.util.List;

public class Generations {

    private final Scorer _scorer;

    public Generations(Scorer scorer){
        _scorer = scorer;
    }

    public Generation next(Generation generation){
        while(generation.hasUnscoredIndividual()){
            Individual individual = generation.getUnscoredIndividual();
            double score = _scorer.score(individual);
            individual.setScore(score);
        }
        List<Individual> survivors = generation.findTopTenPercent();
        List<Individual> successors = new ArrayList<>();
        for(int idx=0; idx<survivors.size(); idx++){
            for(int jdx=0; jdx<survivors.size(); jdx++ ){
                Individual left = survivors.get(idx);
                Individual right = survivors.get(jdx);
                Zygote zygote = new Zygote(left.getGenes(), right.getGenes());
                Individual child = new Individual("Child [" + left.getIdentifier() +  "," + right.getIdentifier() + "]",
                        zygote.getGenes());
                successors.add(child);
            }
        }
        return new Generation(successors);
    }
}
