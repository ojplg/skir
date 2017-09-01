package ojplg.skir.evolve;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Generations {

    private static final Logger _log = LogManager.getLogger(EvolutionRunner.EVOLVE_LOGGER);
    private final Scorer _scorer;

    public Generations(Scorer scorer){
        _scorer = scorer;
    }

    public Generation next(Generation generation){
        while(generation.hasUnscoredIndividual()){
            Individual individual = generation.getUnscoredIndividual();
            double score = _scorer.score(individual);
            _log.info("Individual " + individual.getIdentifier() + " had score " + score + " with genes ["
                    + individual.genesAsJsonObject() + "]");
            individual.setScore(score);
        }
        List<Individual> survivors = generation.findTopIndividuals();
        _log.info("Reduced " + generation.allMembers().size() + " to " + survivors.size() + " survivors");
        List<Individual> successors = new ArrayList<>();
        int number=0;
        int nextGenerationNumber = generation.getNumber() + 1;
        for(int idx=0; idx<survivors.size(); idx++){
            for(int jdx=0; jdx<survivors.size(); jdx++ ){
                Individual left = survivors.get(idx);
                Individual right = survivors.get(jdx);
                Zygote zygote = new Zygote(left.getGenes(), right.getGenes());
                Individual child = new Individual(nextGenerationNumber, number,
                        zygote.getGenes());
                successors.add(child);
                number++;
            }
        }
        _log.info("New generation has size " + successors.size());
        return new Generation(successors, nextGenerationNumber);
    }
}
