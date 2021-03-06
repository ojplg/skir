package ojplg.skir.play.orders;

import ojplg.skir.card.CardSet;
import ojplg.skir.map.Country;
import ojplg.skir.state.Game;
import ojplg.skir.state.GameException;
import ojplg.skir.state.Player;
import ojplg.skir.state.PlayerHoldings;

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
        if( game.getOccupationForce(_conquered) > 0){
            throw new GameException(getGameId(), "Player " + activePlayer() + " issued an occuption order from "
                    + _victor + " to " + _conquered + " although " + _conquered + " still has "
                    + game.getOccupationForce(_conquered) + " armies in it and is owned by "
                    + game.getOccupier(_conquered));
        }

        Player loser = game.getOccupier(_conquered);
        boolean playerEliminated = game.processOccupyOrder(_victor, _conquered, _armies);

        if ( playerEliminated ){
            PlayerHoldings loserHoldings = game.getPlayerHoldings(loser);
            PlayerHoldings activeHoldings = game.getPlayerHoldings(activePlayer());
            boolean cardsAcquired = loserHoldings.getCards().size() > 0;
            game.resolveElimination(activePlayer(), loser);
            if (activeHoldings.hasTooManyCards()){
                return getAdjutant().forConstrainedOrderTypes(ConstrainedOrderType.unconstrainedOrder(OrderType.ExchangeCardSet));
            } else {
                boolean allowCardExchange = cardsAcquired &&
                        CardSet.hasTradeableSet(activeHoldings.getCards());
                return AttackOrderHelper.possibleAttackingOrders(getAdjutant(), game, allowCardExchange);
            }
        } else {
            return AttackOrderHelper.possibleAttackingOrders(getAdjutant(), game);
        }
    }

    @Override
    public OrderType getType() {
        return OrderType.Occupy;
    }

    public Country getVictor() {
        return _victor;
    }

    public Country getConquered() {
        return _conquered;
    }

    public int getArmies() {
        return _armies;
    }
}
