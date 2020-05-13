package ojplg.skir.state;

import ojplg.skir.play.Rolls;

import java.io.Serializable;

public class BattleStats implements Serializable {
    private final double _attackLuckFactor;
    private final int _totalArmiesLostAttacking;
    private final int _totalArmiesKilledAttacking;

    private final double _defenseLuckFactor;
    private final int _totalArmiesLostDefending;
    private final int _totalArmiesKilledDefending;

    public BattleStats() {
        this(0.0, 0, 0, 0.0, 0, 0);
    }

    private BattleStats(double attackLuckFactor, int totalArmiesKilledAttacking, int totalArmiesLostAttacking,
                        double defenseLuckFactor, int totalArmiesKilledDefending, int totalArmiesLostDefending){
        _attackLuckFactor = attackLuckFactor;
        _totalArmiesKilledAttacking = totalArmiesKilledAttacking;
        _totalArmiesLostAttacking = totalArmiesLostAttacking;
        _defenseLuckFactor = defenseLuckFactor;
        _totalArmiesKilledDefending = totalArmiesKilledDefending;
        _totalArmiesLostDefending  = totalArmiesLostDefending;
    }

    public BattleStats updateAttackStats(Rolls rolls){
        return new BattleStats(
                _attackLuckFactor+rolls.attackersExpectationsDifference(),
                _totalArmiesKilledAttacking+rolls.defendersLosses(),
                _totalArmiesLostAttacking+rolls.attackersLosses(),
                _defenseLuckFactor,
                _totalArmiesKilledDefending,
                _totalArmiesLostDefending
        );
    }

    public BattleStats updateDefenseStats(Rolls rolls){
        return new BattleStats(
                _attackLuckFactor,
                _totalArmiesKilledAttacking,
                _totalArmiesLostAttacking,
                _defenseLuckFactor + rolls.defendersExpectationsDifference(),
                _totalArmiesKilledDefending + rolls.attackersLosses(),
                _totalArmiesLostDefending + rolls.defendersLosses()
        );
    }

    public double getAttackLuckFactor() {
        return _attackLuckFactor;
    }

    public double getDefenseLuckFactor() {
        return _defenseLuckFactor;
    }

    public int getTotalArmiesLostAttacking() {
        return _totalArmiesLostAttacking;
    }

    public int getTotalArmiesKilledAttacking() {
        return _totalArmiesKilledAttacking;
    }

    public int getTotalArmiesLostDefending() {
        return _totalArmiesLostDefending;
    }

    public int getTotalArmiesKilledDefending() {
        return _totalArmiesKilledDefending;
    }
}
