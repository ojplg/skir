package ojplg.skir.play.orders;

import ojplg.skir.map.Country;
import ojplg.skir.state.Game;
import ojplg.skir.state.GameException;

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
        if( ! game.getOccupier(_source).equals(game.getOccupier(_destination))){
            throw new GameException(getGameId(), "Player " + getAdjutant().getActivePlayer() + " attempted illegal fortification from "
                + _source + " to " + _destination + ". " + _source + " is owned by " + game.getOccupier(_source)
                    + " and " + _destination + " is owned by " + game.getOccupier(_destination));
        }
        if( ! game.getMap().areNeighbors(_source, _destination)){
            throw new GameException(getGameId(), "Player " + getAdjutant().getActivePlayer() + " attempted illegal fortification from "
                    + _source + " to " + _destination + ". They are not neighbors." );
        }

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
