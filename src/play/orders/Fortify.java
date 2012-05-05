package play.orders;

import map.Country;
import state.Game;
import state.Player;

public class Fortify extends Order {

    private final Country _source;
    private final Country _destination;
    private final int _armies;

    public Fortify(Player player, Country source, Country destination, int armies) {
        super(player);
        _source = source;
        _destination = destination;
        _armies = armies;
    }

    @Override
    TurnPhase execute(Game game) {
        game.fortify(_source, _destination, _armies);
        return TurnPhase.Draw;
    }
}
