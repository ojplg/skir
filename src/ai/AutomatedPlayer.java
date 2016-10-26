package ai;

import play.orders.Adjutant;
import play.orders.OrderType;
import state.Game;
import state.Player;

import java.util.List;

public interface AutomatedPlayer {
    OrderType pickOrder(List<OrderType> possibleOrderTypes, Game game);
    Adjutant executeOrder(OrderType orderType, Adjutant adjutant, Game game);
    Player getPlayer();
}
