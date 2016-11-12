package play.orders;

import map.Country;
import state.Game;

public class AttackUntilVictoryOrDeath extends Order {

    private final Country _attacker;
    private final Country _defender;

    public AttackUntilVictoryOrDeath(Adjutant adjutant, Country attacker, Country defender){
        super(adjutant);
        _attacker = attacker;
        _defender = defender;
    }

    @Override
    public Adjutant execute(Game game) {
        Adjutant finalAdjutant = getAdjutant();
        while(game.getOccupationForce(_attacker) > 1 && game.getOccupationForce(_defender) > 0){
            Attack attack = new Attack(getAdjutant(), _attacker, _defender);
            finalAdjutant = attack.execute(game);
        }
        // BUG!!
        return finalAdjutant;
    }

    @Override
    OrderType getType() {
        return OrderType.AttackUntilVictoryOrDeath;
    }
}
