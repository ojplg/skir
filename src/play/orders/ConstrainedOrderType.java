package play.orders;

import map.Country;
import state.Game;
import state.Player;

import java.util.List;

public class ConstrainedOrderType {

    private final OrderType _orderType;
    private final OrderConstraints _constraints;

    public static ConstrainedOrderType placeArmy(Player player, Game game){
        int maximumArmies = player.reserveCount();
        List<Country> countries = game.countriesOccupied(player);
        PlaceArmyConstraints constraints = new PlaceArmyConstraints(maximumArmies, countries);
        return new ConstrainedOrderType(OrderType.PlaceArmy, constraints);
    }

    public static ConstrainedOrderType occupation(Attack attack, int maximumAvailableForOccupation){
        OccupationConstraints constraints = new OccupationConstraints(attack, maximumAvailableForOccupation);
        return new ConstrainedOrderType(OrderType.Occupy, constraints);
    }

    public static ConstrainedOrderType fortify(Player player, Game game){
        FortificationConstraints constraints = new FortificationConstraints(player, game);
        return new ConstrainedOrderType(OrderType.Fortify, constraints);
    }

    public static ConstrainedOrderType attack(Player player, Game game){
        AttackConstraints constraints = new AttackConstraints(player, game);
        return new ConstrainedOrderType(OrderType.Attack, constraints);
    }

    public static ConstrainedOrderType attackUntilVictoryOrDeath(Player player, Game game){
        AttackConstraints constraints = new AttackConstraints(player, game);
        return new ConstrainedOrderType(OrderType.AttackUntilVictoryOrDeath, constraints);
    }

    public static ConstrainedOrderType unconstrainedOrder(OrderType orderType){
        return new ConstrainedOrderType(orderType, new UnconstrainedOrder());
    }

    private ConstrainedOrderType(OrderType orderType, OrderConstraints orderConstraints){
        this._orderType = orderType;
        this._constraints = orderConstraints;
    }

    public OrderType getOrderType() {
        return _orderType;
    }

    public OrderConstraints getConstraints() {
        return _constraints;
    }

    @Override
    public String toString() {
        return "ConstrainedOrderType{" +
                "_orderType=" + _orderType +
                ", _constraints=" + _constraints +
                '}';
    }
}
