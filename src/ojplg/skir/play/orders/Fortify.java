package ojplg.skir.play.orders;

import ojplg.skir.map.Country;
import ojplg.skir.state.Game;

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
        if( getAdjutant().hasConqueredCountry() && ! activePlayer().hasMaximumCards()){
            return getAdjutant().forOrderType(OrderType.DrawCard);
        } else {
            return Adjutant.nextPlayer(game.nextPlayer());
        }
    }

    @Override
    public OrderType getType() {
        return OrderType.Fortify;
    }
}
