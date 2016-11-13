package play.orders;

import state.Game;
import state.Player;

public class EndTurn extends Order {

    public EndTurn(Adjutant adjutant){
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        Player nextPlayer = game.nextPlayer();
        return Adjutant.nextPlayer(nextPlayer);
    }

    @Override
    public OrderType getType() {
        return OrderType.EndTurn;
    }

}
