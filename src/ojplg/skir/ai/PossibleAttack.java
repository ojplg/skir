package ojplg.skir.ai;

import ojplg.skir.map.Country;

public class PossibleAttack implements Comparable<PossibleAttack> {

    private final Country _attacker;
    private final Country _defender;
    private final int _advantage;

    public PossibleAttack(Country attacker, Country defender, int advantage) {
        this._attacker = attacker;
        this._defender = defender;
        this._advantage = advantage;
    }

    public Country getAttacker() {
        return _attacker;
    }

    public Country getDefender() {
        return _defender;
    }

    public int getAdvantage() {
        return _advantage;
    }

    @Override
    public int compareTo(PossibleAttack other) {
        return other.getAdvantage() - getAdvantage();
    }
}
