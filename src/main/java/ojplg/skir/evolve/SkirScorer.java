package ojplg.skir.evolve;

import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.play.bench.AiTestBench;
import ojplg.skir.play.bench.GameScores;
import ojplg.skir.state.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SkirScorer implements Scorer {

    private static final Logger _log = LogManager.getLogger("ojplg.skir.evolve");

    private final AiTestBench _aiTestBench;
    private final BiFunction<Player, Map<String, Double>, AutomatedPlayer> _testPlayerGenerator;

    private CountDownLatch _latch;
    private GameScores _scores;

    public SkirScorer(AiTestBench aiTestBench, BiFunction<Player, Map<String, Double>, AutomatedPlayer> testPlayerGenerator){
        this._aiTestBench = aiTestBench;
        this._testPlayerGenerator = testPlayerGenerator;
    }

    @Override
    public double score(Individual individual) {
        String name = "Tuner-" + individual.getIdentifier();
        Function<Player, AutomatedPlayer> generator = (Player p) -> {
            p.setDisplayName(name);
            return _testPlayerGenerator.apply(p, individual.getGenes());
        };
        _aiTestBench.setAiToTest(generator);
        _aiTestBench.setResultsConsumer(this::acceptScores);
        _latch = new CountDownLatch(1);
        _aiTestBench.startRun();
        try {
            _latch.await();
        } catch (InterruptedException ie){
            _log.warn("Why was I interrupted?", ie);
        }
        return _scores.getScore(name);
    }

    private void acceptScores(GameScores scores){
        _scores = scores;
        _latch.countDown();
    }

}
