package ojplg.skir.ai;

import ojplg.skir.map.Country;
import ojplg.skir.state.Constants;

public class PossibleAttack implements Comparable<PossibleAttack> {

    private final Country _attacker;
    private final Country _defender;
    private final int _attackerForce;
    private final int _defenderForce;

    public PossibleAttack(Country attacker, Country defender, int attackerForce, int defenderForce) {
        this._attacker = attacker;
        this._defender = defender;
        this._attackerForce = attackerForce;
        this._defenderForce = defenderForce;
    }

    public Country getAttacker() {
        return _attacker;
    }

    public Country getDefender() {
        return _defender;
    }

    public double getAttackerArmyPercentage(){
        return (double) _attackerForce / (_attackerForce + _defenderForce);
    }

    public int getAdvantage() {
        return _attackerForce - _defenderForce;
    }

    public int maximumAttackingDice(){
        return Math.min(Constants.MAXIMUM_ATTACKER_DICE, _attackerForce - 1);
    }

    public int getAttackerForce(){
        return _attackerForce;
    }

    @Override
    public int compareTo(PossibleAttack other) {
        return getAdvantage() - other.getAdvantage();
    }
}
