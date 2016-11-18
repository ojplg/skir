package ojplg.skir.ai;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

import java.util.List;

public interface AutomatedPlayer {
    OrderType pickOrderType(List<OrderType> possibleOrderTypes, Game game);
    Order generateOrder(OrderType orderType, Adjutant adjutant, Game game);
    Player getPlayer();
}
