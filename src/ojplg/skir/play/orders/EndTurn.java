package ojplg.skir.play.orders;

import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

public class EndTurn extends Order {

    public EndTurn(Adjutant adjutant){
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        Player nextPlayer = game.nextPlayer();
        return Adjutant.nextPlayer(nextPlayer, game.getTurnNumber());
    }

    @Override
    public OrderType getType() {
        return OrderType.EndTurn;
    }

}
