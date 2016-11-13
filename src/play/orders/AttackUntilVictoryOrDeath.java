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
            int invadingForce = game.getOccupationForce(_attacker);
            int attackingDice = Math.min(3, invadingForce - 1);
            Attack attack = new Attack(finalAdjutant, _attacker, _defender, attackingDice);
            finalAdjutant = attack.execute(game);
        }
        return finalAdjutant;
    }

    @Override
    public OrderType getType() {
        return OrderType.AttackUntilVictoryOrDeath;
    }
}
