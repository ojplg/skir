package play.orders;

import map.Country;
import state.Game;
import state.Player;

public class PlaceArmy extends Order {

    private final Country _country;

    public PlaceArmy(Player player, Country country){
        super(player);
        _country = country;
    }

    @Override
    public TurnPhase execute(Game game) {
        // TODO: this is the wrong thing to check
        if( activePlayer() != game.getOccupier(_country)){
            throw new RuntimeException("Player " + activePlayer() + " cannot place armies in " + _country);
        }
        game.placeArmy(activePlayer(), _country);
        activePlayer().drawReserves(1);
        if ( activePlayer().hasReserves() ){
            return TurnPhase.Supply;
        }
        return TurnPhase.Attack;
    }
}
