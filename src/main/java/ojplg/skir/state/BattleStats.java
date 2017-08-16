package ojplg.skir.state;

import ojplg.skir.play.Rolls;

public class BattleStats {
    private double _attackLuckFactor = 0.0;
    private double _defenseLuckFactor = 0.0;

    public void updateAttackStats(Rolls rolls){
        _attackLuckFactor+=rolls.attackersExpectationsDifference();
    }

    public void updateDefenseStats(Rolls rolls){
        _defenseLuckFactor+=rolls.defendersExpectationsDifference();
    }


    public double getAttackLuckFactor() {
        return _attackLuckFactor;
    }
    public double getDefenseLuckFactor() {
        return _defenseLuckFactor;
    }
}
