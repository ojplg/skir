package ojplg.skir.play.orders;

import ojplg.skir.map.Country;
import ojplg.skir.state.Game;
import ojplg.skir.state.GameException;

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

    @Override
    public String toString() {
        return "Attack{" +
                "_attackersDiceCount=" + _attackersDiceCount +
                ", _invader=" + _invader +
                ", _target=" + _target +
                '}';
    }

    @Override
    public Adjutant execute(Game game) {
        if( activePlayer() != game.getOccupier(_invader)){
            throw new GameException(getGameId(), "Player " + activePlayer() + " cannot attack from " + _invader);
        }
        if ( activePlayer() == game.getOccupier(_target) ){
            throw new GameException(getGameId(), "Player " + activePlayer() + " trying to attack himself in " + _target);
        }
        boolean conquered = game.processAttackOrder(_invader, _target);
        if ( conquered ){
            return getAdjutant().afterConquest(this, game.getOccupationForce(_invader) - 1);
        } else {
            return AttackOrderHelper.possibleAttackingOrders(getAdjutant(), game);
        }
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
    public OrderType getType() {
        return OrderType.Attack;
    }
}
