package play.orders;

import state.Game;
import state.Player;

public class EndAttacks extends Order {

    public EndAttacks(Player player){
        super(player);
    }

    @Override
    TurnPhase execute(Game game) {
        return TurnPhase.Move;
    }
}
