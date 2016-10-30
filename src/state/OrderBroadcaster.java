package state;

import play.orders.OrderType;

import java.util.ArrayList;
import java.util.List;

public class OrderBroadcaster {

    private List<OrderEventListener> _orderEventListeners = new ArrayList<OrderEventListener>();

    public void addListener(OrderEventListener listener){
        _orderEventListeners.add(listener);
    }

    public void possibleOrderTypes(Player player, List<OrderType> orderTypes){
        for(OrderEventListener listener : _orderEventListeners){
            if (listener != null){
                listener.possibleOrders(player, orderTypes);
            }
        }

    }

}
