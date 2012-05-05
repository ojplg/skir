package play.orders;

import map.Country;
import state.Game;
import state.Player;

public class Occupy extends Order {

    private Country _victor;
    private Country _conquered;
    private int _armies;

    public Occupy(Player player, Country victor, Country conquered, int armies) {
        super(player);
    }

    @Override
    TurnPhase execute(Game game) {
        Player loser = game.getOccupier(_conquered);
        if ( game.resolveConquest(_victor, _conquered, _armies) ){
            game.resolveElimination(activePlayer(), loser);
            return TurnPhase.Supply;
        }
        return TurnPhase.Attack;
    }
}
