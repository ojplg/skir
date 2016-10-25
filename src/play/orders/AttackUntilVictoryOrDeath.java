package play.orders;

import map.Country;
import play.Roller;
import state.Game;

public class AttackUntilVictoryOrDeath extends Order {

    private final Country _attacker;
    private final Country _defender;
    private final Roller _roller;

    public AttackUntilVictoryOrDeath(Adjutant adjutant, Country attacker, Country defender, Roller roller){
        super(adjutant);
        _attacker = attacker;
        _defender = defender;
        _roller = roller;
    }

    @Override
    public Adjutant execute(Game game) {
        while(game.getOccupationForce(_attacker) > 1 && game.getOccupationForce(_defender) > 0){
            int dieCount = Math.min(3, game.getOccupationForce(_attacker) - 1);
            Attack attack = new Attack(getAdjutant(), _roller, _attacker, _defender, dieCount);
            attack.execute(game);
        }
        return getAdjutant();
    }

    @Override
    OrderType getType() {
        return OrderType.AttackUntilVictoryOrDeath;
    }
}
