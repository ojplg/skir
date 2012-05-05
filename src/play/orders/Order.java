package play.orders;

import state.Game;
import state.Player;

public abstract class Order {

    private final Player _player;

    public Order (Player player){
        _player = player;
    }

    abstract TurnPhase execute(Game game);

    public Player activePlayer(){
        return _player;
    }
}
