package play.orders;

import map.Country;
import play.Roller;
import state.Constants;
import state.Game;
import play.Rolls;

public class Attack extends Order {

    private final int _attackersDiceCount;
    private final Country _invader;
    private final Country _target;

    public Attack(Adjutant adjutant, Country invader, Country target, int attackersDiceCount) {
        super(adjutant);
        _attackersDiceCount = attackersDiceCount;
        _invader = invader;
        _target = target;
    }

    public Attack(Adjutant adjutant, Country invader, Country target){
        super(adjutant);
        _attackersDiceCount = 3;
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
        //int numberDefenders = game.getOccupationForce(_target);
        boolean conquered = game.resolveAttack(_invader, _target);
        if ( conquered ){
            getAdjutant().successfulAttack(this);
            getAdjutant().setAllowableOrders(OrderType.Occupy);
        } else {
            getAdjutant().setAllowableOrders(OrderType.Attack, OrderType.EndAttacks, OrderType.AttackUntilVictoryOrDeath);
        }
        return getAdjutant();
    }

    public int getAttackersDiceCount() {
        return _attackersDiceCount;
    }

    public Country getInvader() {
        return _invader;
    }

    public Country getTarget() {
        return _target;
    }

    @Override
    OrderType getType() {
        return OrderType.Attack;
    }
}
