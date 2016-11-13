package play.orders;

import map.Country;
import state.Game;
import state.Player;

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
        boolean playerEliminated = game.resolveConquest(_victor, _conquered, _armies);
        ConstrainedOrderType attack = ConstrainedOrderType.attack(activePlayer(), game);
        ConstrainedOrderType attackUntilVictoryOrDeath = ConstrainedOrderType.attackUntilVictoryOrDeath(activePlayer(), game);
        ConstrainedOrderType endAttacks = ConstrainedOrderType.unconstrainedOrder(OrderType.EndAttacks);

        Player loser = game.getOccupier(_conquered);
        if ( playerEliminated ){
            // TODO: Check for game over?
            game.resolveElimination(activePlayer(), loser);
            if (activePlayer().hasTooManyCards()){
                return getAdjutant().forOrderTypes(OrderType.ExchangeCardSet);
            } else {
                return getAdjutant().forConstrainedOrderTypes(attack, attackUntilVictoryOrDeath, endAttacks);
            }
        } else {
            return getAdjutant().forConstrainedOrderTypes(attack, attackUntilVictoryOrDeath, endAttacks);
        }
    }

    @Override
    public OrderType getType() {
        return OrderType.Occupy;
    }
}
