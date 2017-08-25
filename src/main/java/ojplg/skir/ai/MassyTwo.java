package ojplg.skir.ai;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

public class MassyTwo implements AutomatedPlayer {

    private static final String NAME = "MassyTwo";
    private final Player _me;

    private AutomatedPlayer _personality;

    public MassyTwo(Player player){
        _me = player;
        _me.setDisplayName(NAME);
    }

    @Override
    public void initialize(Game game) {
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {
        if( _personality == null ) {
            if (AiUtils.hasMajorityArmies(_me, game)) {
                _personality = new Bully(_me, NAME);
            } else {
                _personality = new Massy(_me, NAME);
            }
        }
        Order order =  _personality.generateOrder(adjutant, game);
        if (order.getType() == OrderType.DrawCard || order.getType() == OrderType.EndTurn){
            _personality = null;
        }
        return order;
    }

    @Override
    public Player getPlayer() {
        return _me;
    }

}
