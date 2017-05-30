package ojplg.skir.ai;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

public interface AutomatedPlayer {
    void initialize(Game game);
    Order generateOrder(Adjutant adjutant, Game game);
    Player getPlayer();
    Object getIdentification();
}