package ai;

import play.orders.Adjutant;
import play.orders.Order;
import play.orders.OrderType;
import state.Game;
import state.Player;

import java.util.List;

public interface AutomatedPlayer {
    OrderType pickOrderType(List<OrderType> possibleOrderTypes, Game game);
    Order generateOrder(OrderType orderType, Adjutant adjutant, Game game);
    Player getPlayer();
}
