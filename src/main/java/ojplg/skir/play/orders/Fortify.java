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
        game.processFortifyOrder(_source, _destination, _armies);
        if( getAdjutant().hasConqueredCountry() && ! game.getPlayerHoldings(activePlayer()).hasMaximumCards()){
            return getAdjutant().forConstrainedOrderTypes(ConstrainedOrderType.unconstrainedOrder(OrderType.DrawCard));
        } else {
            return getAdjutant().nextPlayer(game.nextPlayer(), game.getTurnNumber());
        }
    }

    public Country getSource() {
        return _source;
    }

    public Country getDestination() {
        return _destination;
    }

    public int getArmies() {
        return _armies;
    }

    @Override
    public OrderType getType() {
        return OrderType.Fortify;
    }
}
