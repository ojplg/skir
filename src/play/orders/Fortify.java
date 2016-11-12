package play.orders;

import map.Country;
import state.Game;
import state.Player;

public class Fortify extends Order {

    private final Country _source;
    private final Country _destination;
    private final int _armies;

    public Fortify(Adjutant adjutant, Country source, Country destination, int armies) {
        super(adjutant);
        _source = source;
        _destination = destination;
        _armies = armies;
    }

    @Override
    public Adjutant execute(Game game) {
        game.fortify(_source, _destination, _armies);
        if( getAdjutant().hasConqueredCountry() ){
            return getAdjutant().forOrderType(OrderType.DrawCard);
        } else {
            return Adjutant.nextPlayer(game.nextPlayer());
        }
    }

    @Override
    OrderType getType() {
        return OrderType.Fortify;
    }
}
