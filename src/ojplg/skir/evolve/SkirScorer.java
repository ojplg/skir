package ojplg.skir.evolve;

import ojplg.skir.ai.Tuner;
import ojplg.skir.play.bench.AiTestBench;
import ojplg.skir.play.bench.GameScores;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;

public class SkirScorer implements Scorer {

    private static final Logger _log = LogManager.getLogger(SkirScorer.class);

    private final AiTestBench _aiTestBench;

    private CountDownLatch _latch;
    private GameScores _scores;

    public SkirScorer(AiTestBench aiTestBench){
        this._aiTestBench = aiTestBench;
    }

    @Override
    public double score(Individual individual) {
        _aiTestBench.setAiToTest(p -> new Tuner(p, individual.getGenes(), "TunerTesting!"));
        _aiTestBench.setResultsConsumer(this::acceptScores);
        _latch = new CountDownLatch(1);
        _aiTestBench.start();
        try {
            _latch.await();
        } catch (InterruptedException ie){
            _log.warn("Why was I interrupted?", ie);
        }
        return _scores.getScore("TunerTesting!");
    }

    private void acceptScores(GameScores scores){
        _scores = scores;
        _latch.countDown();
    }

}
