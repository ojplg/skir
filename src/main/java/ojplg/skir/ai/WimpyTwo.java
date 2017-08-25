package ojplg.skir.ai;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

/**
 * Follows the path of Wimpy, but if it is ever determined
 * that its owned armies exceed all opponent armies, it turns
 * into Bully.
 */
public class WimpyTwo implements AutomatedPlayer {

    private final static String NAME = "WimpyTwo";

    private final Player _me;

    public WimpyTwo(Player player){
        _me = player;
        player.setDisplayName("WimpyTwo");
    }

    @Override
    public void initialize(Game game) {
        // do nothing
    }

    public Player getPlayer(){
        return _me;
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game){
        AutomatedPlayer currentStrategy;
        if( AiUtils.hasMajorityArmies(_me, game)){
            currentStrategy = new Bully(_me, NAME);
        } else {
            currentStrategy = new Wimpy(_me, NAME);
        }
        return currentStrategy.generateOrder(adjutant, game);
    }
}
