package play.orders;

import state.Game;
import state.Player;

public class ClaimArmies extends Order {

    public ClaimArmies(Player player) {
        super(player);
    }

    @Override
    TurnPhase execute(Game game) {
        int geographicArmies = game.computeMapSupply(activePlayer());
        activePlayer().grantReserves(geographicArmies);
        return TurnPhase.Supply;
    }
}
