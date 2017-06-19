package ojplg.skir.play.orders;

import ojplg.skir.state.Game;
import ojplg.skir.state.GameId;
import ojplg.skir.state.Player;
import ojplg.skir.state.event.GameSpecifiable;

public abstract class Order implements GameSpecifiable {

    private final Adjutant _adjutant;

    public Order (Adjutant adjutant){
        _adjutant = adjutant;
    }

    public abstract Adjutant execute(Game game);
    public abstract OrderType getType();

    public Player activePlayer(){
        return _adjutant.getActivePlayer();
    }

    public Adjutant getAdjutant(){
        return _adjutant;
    }

    public GameId getGameId(){
        return _adjutant.getGameId();
    }

}
