package ojplg.skir.play.orders;

import ojplg.skir.state.Game;

public class AttackOrderHelper {

    public static Adjutant possibleAttackingOrders(Adjutant adjutant, Game game) {
        return possibleAttackingOrders(adjutant, game, false);
    }

    public static Adjutant possibleAttackingOrders(Adjutant adjutant, Game game, boolean allowCardExchage){
        ConstrainedOrderType exchangeCards = ConstrainedOrderType.unconstrainedOrder(OrderType.ExchangeCardSet);
        if( game.hasPossibleAttack(adjutant.getActivePlayer())) {
            ConstrainedOrderType attack = ConstrainedOrderType.attack(adjutant.getActivePlayer(), game);
            ConstrainedOrderType attackUntilVictoryOrDeath = ConstrainedOrderType.attackUntilVictoryOrDeath(adjutant.getActivePlayer(), game);
            ConstrainedOrderType endAttacks = ConstrainedOrderType.unconstrainedOrder(OrderType.EndAttacks);
            if( allowCardExchage ) {
                return adjutant.forConstrainedOrderTypes(attack, attackUntilVictoryOrDeath, endAttacks, exchangeCards);
            } else {
                return adjutant.forConstrainedOrderTypes(attack, attackUntilVictoryOrDeath, endAttacks);
            }
        } else if( game.hasLegalFortification(adjutant.getActivePlayer()) && adjutant.hasConqueredCountry()) {
            ConstrainedOrderType fortify = ConstrainedOrderType.fortify(adjutant.getActivePlayer(), game);
            ConstrainedOrderType endTurn = ConstrainedOrderType.unconstrainedOrder(OrderType.EndTurn);
            ConstrainedOrderType drawCard = ConstrainedOrderType.unconstrainedOrder(OrderType.DrawCard);
            if( allowCardExchage ){
                return adjutant.forConstrainedOrderTypes(fortify, endTurn, drawCard, exchangeCards);
            } else {
                return adjutant.forConstrainedOrderTypes(fortify, endTurn, drawCard);
            }
        } else if( game.hasLegalFortification(adjutant.getActivePlayer())) {
            ConstrainedOrderType fortify = ConstrainedOrderType.fortify(adjutant.getActivePlayer(), game);
            ConstrainedOrderType endTurn = ConstrainedOrderType.unconstrainedOrder(OrderType.EndTurn);
            if( allowCardExchage ){
                return adjutant.forConstrainedOrderTypes(fortify, endTurn, exchangeCards);
            } else {
                return adjutant.forConstrainedOrderTypes(fortify, endTurn);
            }
        } else {
            ConstrainedOrderType endTurn = ConstrainedOrderType.unconstrainedOrder(OrderType.EndTurn);
            if ( allowCardExchage ) {
                return adjutant.forConstrainedOrderTypes(endTurn, exchangeCards);
            } else {
                return adjutant.forConstrainedOrderTypes(endTurn);
            }
        }
    }
}
