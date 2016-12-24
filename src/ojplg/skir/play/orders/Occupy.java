package ojplg.skir.play.orders;

import ojplg.skir.map.Country;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

public class Occupy extends Order {

    private Country _victor;
    private Country _conquered;
    private int _armies;

    public Occupy(Adjutant adjutant, Country victor, Country conquered, int armies) {
        super(adjutant);
        _victor = victor;
        _conquered = conquered;
        _armies = armies;
    }

    @Override
    public Adjutant execute(Game game) {
        Player loser = game.getOccupier(_conquered);
        boolean playerEliminated = game.processOccupyOrder(_victor, _conquered, _armies);

        if ( playerEliminated ){
            // TODO: Check for game over?
            game.resolveElimination(activePlayer(), loser);
            if (activePlayer().hasTooManyCards()){
                return getAdjutant().forConstrainedOrderTypes(ConstrainedOrderType.unconstrainedOrder(OrderType.ExchangeCardSet));
            } else {
                return AttackOrderHelper.possibleAttackingOrders(getAdjutant(), game);
            }
        } else {
            return AttackOrderHelper.possibleAttackingOrders(getAdjutant(), game);
        }
    }

    @Override
    public OrderType getType() {
        return OrderType.Occupy;
    }
}
