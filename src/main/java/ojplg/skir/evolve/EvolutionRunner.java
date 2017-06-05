package ojplg.skir.evolve;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.Tuney;
import ojplg.skir.play.Channels;
import ojplg.skir.play.bench.AiTestBench;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.ThreadFiber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EvolutionRunner {

    private final static Logger _log = LogManager.getLogger("ojplg.skir.evolve");

    private final Channels _channels;
    private final ThreadFiber _evolveThread;
    private final int _numberOfGenerations = 100;

    public EvolutionRunner(Channels channels, ThreadFiber evolveThread){
        _channels = channels;
        _evolveThread = evolveThread;
    }

    public void evolve(AiFactory aiFactory){
        _log.info("Evolving");
        AiTestBench bench = new AiTestBench(aiFactory, _channels, _evolveThread, 25);
        SkirScorer scorer = new SkirScorer(bench);
        scorer.start();
        Generations generations = new Generations(scorer);
        Generation currentGeneration = createFirstGeneration();
        for(int cnt=0; cnt < _numberOfGenerations ; cnt++) {
            Generation nextGeneration = generations.next(currentGeneration);
            _log.info("next generation determined with " + nextGeneration.allMembers().size() + " individuals");
            currentGeneration = nextGeneration;
        }
    }

//    private void logGeneration(int number, Generation generation){
//        _log.info("Logging generation " + number + " has " + generation.allMembers().size() + " individuals.");
//        for (Individual individual:generation.allMembers()) {
//            Map<String, Double> genes = individual.getGenes();
//            JSONObject jObject = new JSONObject(genes);
//            _log.info("Generation " + number + " named " + individual.getIdentifier() + " JSON: " + jObject);
//        }
//    }

    private Generation createFirstGeneration(){
        List<Individual> randoms = new ArrayList<>();
        for(int idx=0; idx<64; idx++){
            randoms.add(generateRandomTunerGenes(idx));
        }
        return new Generation(randoms,0);
    }

    private Individual generateRandomTunerGenes(int number){
        Map<String, Double> genes = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        for (Map.Entry<String, Double> gene : Tuney.presetTunings().entrySet() ) {
            double fuzz = random.nextDouble() / 2;
            fuzz += 0.75;
            double value = Math.min(0.99999, gene.getValue() * fuzz);
            genes.put(gene.getKey(), value);
        }
        return new Individual(0, number, genes);
    }

}
