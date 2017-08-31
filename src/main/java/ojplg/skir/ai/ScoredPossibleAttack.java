package ojplg.skir.ai;

public class ScoredPossibleAttack implements Comparable<ScoredPossibleAttack> {

    private final PossibleAttack _possibleAttack;
    private final Double _score;

    public ScoredPossibleAttack(PossibleAttack possibleAttack, double score){
        _possibleAttack = possibleAttack;
        _score = score;
    }

    public PossibleAttack getPossibleAttack(){
        return _possibleAttack;
    }

    public double getScore(){
        return _score;
    }

    @Override
    public int compareTo(ScoredPossibleAttack other) {
        return this._score.compareTo(other._score);
    }
}
