package state;

import play.orders.OrderType;

import java.util.List;

public interface OrderEventListener {

    void possibleOrders(Player player, List<OrderType> possibilities);

}
