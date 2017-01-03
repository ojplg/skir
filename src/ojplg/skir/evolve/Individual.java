package ojplg.skir.evolve;

import java.util.Collections;
import java.util.Map;

public class Individual implements Comparable<Individual> {

    private final String _identifier;
    private final Map<String, Double> _genes;
    private Double _score = null;

    public Individual(String identifier, Map<String, Double> genes){
        _genes = Collections.unmodifiableMap(genes);
        _identifier = identifier;
    }

    public void setScore(double score){
        _score = score;
    }

    public String getIdentifier() {
        return _identifier;
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
