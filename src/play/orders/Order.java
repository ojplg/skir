package play.orders;

import state.Game;
import state.Player;

public abstract class Order {

    private final Adjutant _adjutant;

    public Order (Adjutant adjutant){
        _adjutant = adjutant;
    }

    public abstract Adjutant execute(Game game);
    abstract OrderType getType();

    public Player activePlayer(){
        return _adjutant.getActivePlayer();
    }

    public Adjutant getAdjutant(){
        return _adjutant;
    }

}
