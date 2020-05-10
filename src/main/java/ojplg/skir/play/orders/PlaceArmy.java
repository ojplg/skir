package ojplg.skir.play.orders;

import ojplg.skir.map.Country;
import ojplg.skir.state.Game;
import ojplg.skir.state.GameException;

public class PlaceArmy extends Order {

    private final Country _country;
    private final int _count;

    public PlaceArmy(Adjutant adjutant, Country country){
        super(adjutant);
        _country = country;
        _count = 1;
    }

    public PlaceArmy(Adjutant adjutant, Country country, int count){
        super(adjutant);
        _country = country;
        _count = count;
    }

    @Override
    public Adjutant execute(Game game) {
        if( activePlayer() != game.getOccupier(_country)){
            throw new GameException(getGameId(), "Player " + activePlayer() + " cannot place armies in " + _country
               + " Player owns " + game.getPlayerHoldings(activePlayer()));
        }
        game.processPlaceArmyOrder(activePlayer(), _country, _count);
        if ( game.getPlayerHoldings(activePlayer()).hasReserves() ){
            return getAdjutant().forConstrainedOrderTypes(ConstrainedOrderType.placeArmy(activePlayer(), game));
        } else {
            return AttackOrderHelper.possibleAttackingOrders(getAdjutant(), game);
        }
    }

    @Override
    public OrderType getType() {
        return OrderType.PlaceArmy;
    }

    public Country getCountry() {
        return _country;
    }

    public int getCount() {
        return _count;
    }
}
