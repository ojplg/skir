package ojplg.skir.evolve;

import ojplg.skir.ai.Tuner;
import ojplg.skir.play.bench.AiTestBench;

public class SkirScorer implements Scorer {

    private final AiTestBench _aiTestBench;

    public SkirScorer(AiTestBench aiTestBench){
        this._aiTestBench = aiTestBench;
    }

    @Override
    public double score(Individual individual) {
        _aiTestBench.setAiToTest(p -> new Tuner(p, individual.getGenes(), "TunerTesting!"));
        _aiTestBench.start();

        return 0;
    }

}
