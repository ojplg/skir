package ojplg.skir.evolve;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
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
import java.util.Set;
import java.util.function.BiFunction;

public class EvolutionRunner {

    public static final String EVOLVE_LOGGER = "ojplg.skir.evolve";

    private final static Logger _log = LogManager.getLogger(EVOLVE_LOGGER);

    private final Channels _channels;
    private final ThreadFiber _evolveThread;
    private final AiFactory _aiFactory;
    private final Set<String> _geneNames;
    private final BiFunction<Player, Map<String,Double>, AutomatedPlayer> _testPlayerGenerator;
    private final EvolutionSettings _evolutionSettings;

    private GameRunner _gameRunner;

    public EvolutionRunner(AiFactory aiFactory, Channels channels, ThreadFiber evolveThread, EvolutionSettings evolutionSettings){
        _channels = channels;
        _evolveThread = evolveThread;
        _geneNames = evolutionSettings.getSettingNames();
        _testPlayerGenerator = evolutionSettings.getPlayerGenerator();
        _aiFactory =aiFactory;
        _evolutionSettings = evolutionSettings;
    }

    public void start(){
        _log.info("Evolving with settings " + _evolutionSettings);
        AiTestBench bench = new AiTestBench(_aiFactory, _channels, _evolveThread, _evolutionSettings.getGamesPerIndividual());
        SkirScorer scorer = new SkirScorer(bench, _testPlayerGenerator);
        bench.start();
        Generations generations = new Generations(scorer);
        Generation currentGeneration = createFirstGeneration();
        for(int cnt = 0; cnt < _evolutionSettings.getNumberGenerations(); cnt++) {
            Generation nextGeneration = generations.next(currentGeneration);
            _log.info("next generation determined with " + nextGeneration.allMembers().size() + " individuals");
            currentGeneration = nextGeneration;
        }
    }

    private Generation createFirstGeneration(){
        List<Individual> randoms = new ArrayList<>();
        for(int idx=0; idx<_evolutionSettings.getGenerationSize(); idx++){
            randoms.add(generateRandomTunerGenes(idx));
        }
        return new Generation(randoms,0);
    }

    private Individual generateRandomTunerGenes(int number){
        Map<String, Double> genes = new HashMap<>();
        Random random = new Random(number);
        for (String gene : _geneNames ) {
            genes.put(gene, random.nextDouble());
        }
        return new Individual(0, number, genes);
    }

}
