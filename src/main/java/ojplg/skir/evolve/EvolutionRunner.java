package ojplg.skir.evolve;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.ai.TuneyTwo;
import ojplg.skir.play.Channels;
import ojplg.skir.play.GameRunner;
import ojplg.skir.play.NewGameRequest;
import ojplg.skir.play.bench.AiTestBench;
import ojplg.skir.state.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.ThreadFiber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

public class EvolutionRunner {

    public static final String EVOLVE_LOGGER = "ojplg.skir.evolve";

    private final static Logger _log = LogManager.getLogger(EVOLVE_LOGGER);

    private final Channels _channels;
    private final ThreadFiber _evolveThread;
    private final AiFactory _aiFactory;
    private final Map<String, Double> _presetTunings;
    private final BiFunction<Player, Map<String,Double>, AutomatedPlayer> _testPlayerGenerator;

    private final int GAMES_PER_TRIAL = 50;
    private final int NUMBER_OF_GENERATIONS = 50;
    private final int GENERATION_SIZE = 64;

    private GameRunner _gameRunner;

    public EvolutionRunner(AiFactory aiFactory, Channels channels, ThreadFiber evolveThread){
        this(aiFactory, channels, evolveThread,
                TuneyTwo.presetTunings(),
                (p, t) -> new TuneyTwo(p, t));
    }

    public EvolutionRunner(AiFactory aiFactory, Channels channels, ThreadFiber evolveThread,
                           Map<String, Double> presetTunings, BiFunction<Player, Map<String,Double>, AutomatedPlayer> testPlayerGenerator){
        _channels = channels;
        _evolveThread = evolveThread;
        _presetTunings = presetTunings;
        _testPlayerGenerator = testPlayerGenerator;
        _aiFactory =aiFactory;
    }

    public void start(){
        _log.info("Evolving");
        AiTestBench bench = new AiTestBench(_aiFactory, _channels, _evolveThread, GAMES_PER_TRIAL);
        SkirScorer scorer = new SkirScorer(bench, _testPlayerGenerator);
        scorer.start();
        Generations generations = new Generations(scorer);
        Generation currentGeneration = createFirstGeneration();
        for(int cnt = 0; cnt < NUMBER_OF_GENERATIONS; cnt++) {
            Generation nextGeneration = generations.next(currentGeneration);
            _log.info("next generation determined with " + nextGeneration.allMembers().size() + " individuals");
            currentGeneration = nextGeneration;
        }
        setUpNewGameRunner();
    }

    private void setUpNewGameRunner(){
        if( _gameRunner != null){
            _gameRunner.stop();
        }
        _gameRunner = new GameRunner(_aiFactory, _channels, NewGameRequest.aiEvolution());
        _gameRunner.start();
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
        for(int idx=0; idx<GENERATION_SIZE; idx++){
            randoms.add(generateRandomTunerGenes(idx));
        }
        return new Generation(randoms,0);
    }

    private Individual generateRandomTunerGenes(int number){
        Map<String, Double> genes = new HashMap<>();
        Random random = new Random(System.currentTimeMillis());
        for (Map.Entry<String, Double> gene : _presetTunings.entrySet() ) {
            double fuzz = random.nextDouble() / 2;
            fuzz += 0.75;
            double value = Math.min(0.99999, gene.getValue() * fuzz);
            genes.put(gene.getKey(), value);
        }
        return new Individual(0, number, genes);
    }

}
