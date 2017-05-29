package ojplg.skir.evolve;

import java.util.Collections;
import java.util.Map;

public class Individual implements Comparable<Individual> {

    private final int _individualNumber;
    private final int _generationNumber;
    private final Map<String, Double> _genes;
    private Double _score = null;

    public Individual(int generationNumber, int individualNumber, Map<String, Double> genes){
        _genes = Collections.unmodifiableMap(genes);
        _individualNumber = individualNumber;
        _generationNumber = generationNumber;
    }

    public void setScore(double score){
        _score = score;
    }

    public String getIdentifier() {
        return _generationNumber + "." + _individualNumber;
    }

    public Map<String, Double> getGenes() {
        return _genes;
    }

    public double getScore() {
        return _score;
    }

    public boolean isUnscored(){
        return _score == null;
    }

    @Override
    public int compareTo(Individual o) {
        return this._score.compareTo(o._score);
    }
}
