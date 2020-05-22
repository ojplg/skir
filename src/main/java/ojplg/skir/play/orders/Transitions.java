package ojplg.skir.play.orders;

import java.util.Arrays;
import java.util.List;

public class Transitions {

    public static List<OrderType> possibleTransition(OrderType orderType){
        switch (orderType) {
            case ExchangeCardSet:
                return Arrays.asList(OrderType.ClaimArmies, OrderType.PlaceArmy);
            case ClaimArmies :
                return Arrays.asList(OrderType.PlaceArmy, OrderType.ExchangeCardSet);
            case PlaceArmy:
                return Arrays.asList(OrderType.PlaceArmy, OrderType.Attack, OrderType.AttackUntilVictoryOrDeath, OrderType.EndAttacks, OrderType.Fortify, OrderType.EndTurn);
            case Attack:
            case AttackUntilVictoryOrDeath:
                return Arrays.asList(OrderType.Attack, OrderType.AttackUntilVictoryOrDeath, OrderType.EndAttacks, OrderType.Occupy, OrderType.EndTurn, OrderType.Fortify);
            case Occupy:
                return Arrays.asList(OrderType.Attack, OrderType.AttackUntilVictoryOrDeath, OrderType.EndAttacks, OrderType.EndTurn, OrderType.Fortify,
                        OrderType.ExchangeCardSet, OrderType.ClaimArmies, OrderType.DrawCard);
            case EndAttacks:
                return Arrays.asList(OrderType.DrawCard, OrderType.Fortify, OrderType.EndTurn, OrderType.ClaimArmies);
            case Fortify:
                return Arrays.asList(OrderType.DrawCard, OrderType.EndTurn, OrderType.ClaimArmies, OrderType.ExchangeCardSet);
            case DrawCard:
                return Arrays.asList(OrderType.EndTurn, OrderType.ClaimArmies, OrderType.ExchangeCardSet);
            case EndTurn:
                return Arrays.asList(OrderType.ExchangeCardSet, OrderType.ClaimArmies, OrderType.PlaceArmy);
            default:
                throw new UnsupportedOperationException("missing order type: " + orderType);
        }
    }

    public static boolean isPossibleTransition(OrderType priorOrderType, OrderType currentOrderType){
        return possibleTransition(priorOrderType).contains(currentOrderType);
    }

}
