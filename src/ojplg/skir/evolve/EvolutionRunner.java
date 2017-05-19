package ojplg.skir.evolve;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.Tuner;
import ojplg.skir.play.Channels;
import ojplg.skir.play.bench.AiTestBench;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.ThreadFiber;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EvolutionRunner {

    private final static Logger _log = LogManager.getLogger(EvolutionRunner.class);

    private final Channels _channels;
    private final ThreadFiber _evolveThread;
    private final int _numberOfGenerations = 10;


    public EvolutionRunner(Channels channels, ThreadFiber evolveThread){
        _channels = channels;
        _evolveThread = evolveThread;
    }

    public void evolve(AiFactory aiFactory){
        _log.info("Evolving");
        AiTestBench bench = new AiTestBench(aiFactory, _channels, _evolveThread, 10);
        SkirScorer scorer = new SkirScorer(bench);
        Generations generations = new Generations(scorer);
        Generation currentGeneration = createFirstGeneration();
        for(int generation=0; generation < _numberOfGenerations ; generation++) {
            Generation nextGeneration = generations.next(currentGeneration);
            _log.info("next generation determined");
            logGeneration(generation, nextGeneration);
            currentGeneration = nextGeneration;
        }
    }

    private void logGeneration(int number, Generation generation){
        for (Individual individual:generation.findTopTenPercent()) {
            Map<String, Double> genes = individual.getGenes();
            JSONObject jObject = new JSONObject(genes);
            _log.info("Individual in generation " + number + ": " + jObject);
        }
    }

    private Generation createFirstGeneration(){
        List<Individual> randoms = new ArrayList<>();
        for(int idx=0; idx<100; idx++){
            randoms.add(generateRandomTunerGenes());
        }
        return new Generation(randoms);
    }

    private Individual generateRandomTunerGenes(){
        Map<String, Double> genes = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        for (String gene : Tuner.tuningKeys() ) {
            genes.put(gene, random.nextDouble());
        }
        // TODO: Does this need a name?
        return new Individual("fred", genes);
    }

}
