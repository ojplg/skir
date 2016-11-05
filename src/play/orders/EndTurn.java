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
        return new Adjutant(nextPlayer);
    }

    @Override
    OrderType getType() {
        return OrderType.EndTurn;
    }

}
