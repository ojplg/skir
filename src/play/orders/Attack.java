package play.orders;

import map.Country;
import play.Roller;
import state.Constants;
import state.Game;
import state.Player;
import state.Rolls;

public class Attack extends Order {

    private final int _attackersDiceCount;
    private final Country _invader;
    private final Country _target;
    private final Roller _roller;

    public Attack(Adjutant adjutant, Roller roller, Country invader, Country target, int attackersDiceCount) {
        super(adjutant);
        _roller = roller;
        _attackersDiceCount = attackersDiceCount;
        _invader = invader;
        _target = target;
    }

    @Override
    public Adjutant execute(Game game) {
        if( activePlayer() != game.getOccupier(_invader)){
            throw new RuntimeException("Player " + activePlayer() + " cannot attack from " + _invader);
        }
        if ( activePlayer() == game.getOccupier(_target) ){
            throw new RuntimeException("Player " + activePlayer() + " trying to attack himself in " + _target);
        }
        // TODO: Allow a defender to use 1 die?
        int numberDefenders = game.getOccupationForce(_target);
        int dice = Math.min(numberDefenders, Constants.MAXIMUM_DEFENDER_DICE);
        Rolls rolls = _roller.roll(_attackersDiceCount, dice);
        boolean conquered = game.resolveAttack(_invader, _target, rolls);
        if ( conquered ){
            getAdjutant().setAllowableOrders(OrderType.Occupy);
        } else {
            getAdjutant().setAllowableOrders(OrderType.Attack, OrderType.EndAttacks);
        }
        return getAdjutant();
    }

    @Override
    OrderType getType() {
        return OrderType.Attack;
    }
}
