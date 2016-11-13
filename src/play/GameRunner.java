package play;

import ai.AutomatedPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import play.orders.Adjutant;
import play.orders.Order;
import play.orders.OrderType;
import state.Game;
import state.Player;
import state.event.ClientConnectedEvent;

import java.util.HashMap;
import java.util.Map;

public class GameRunner {

    private final static Logger _log = LogManager.getLogger(GameRunner.class);

    private final Map<Player,AutomatedPlayer> _automatedPlayers = new HashMap<Player,AutomatedPlayer>();
    private final Game _game;
    private final Channels _channels;
    private final Fiber _fiber;

    private Adjutant _currentAdjutant;

    public GameRunner(Game game, Channels channels, Fiber fiber){
        _game = game;
        _channels = channels;
        _fiber = fiber;

        _channels.OrderEnteredChannel.subscribe(_fiber,
                new Callback<Order>() {
                    @Override
                    public void onMessage(Order order) {
                        processOrder(order);
                    }
                });
        _channels.ClientConnectedEventChannel.subscribe(_fiber,
                new Callback<ClientConnectedEvent>() {
                    @Override
                    public void onMessage(ClientConnectedEvent clientConnectedEvent) {
                        _channels.AdjutantChannel.publish(_currentAdjutant);
                    }
                });
    }

    private void processOrder(Order order){
        _log.info("Processing order for " + _currentAdjutant.getActivePlayer() + " of type " + order.getType());
        _currentAdjutant = order.execute(_game);
        AutomatedPlayer ai = getAutomatedPlayer(_currentAdjutant.getActivePlayer());
        if( ai != null ){
            OrderType ot = ai.pickOrderType(_currentAdjutant.allowableOrders(), _game);
            Order generatedOrder = ai.generateOrder(ot, _currentAdjutant, _game);
            processOrder(generatedOrder);
        } else {
            _channels.AdjutantChannel.publish(_currentAdjutant);
        }
    }

    public void startGame(){
        _currentAdjutant = Adjutant.nextPlayer(_game.currentAttacker());
        _channels.AdjutantChannel.publish(_currentAdjutant);
    }

    public void addAutomatedPlayer(AutomatedPlayer ai){
        _log.info("Adding automated player for " + ai.getPlayer());
        _automatedPlayers.put(ai.getPlayer(),ai);
    }

    public AutomatedPlayer getAutomatedPlayer(Player player){
        AutomatedPlayer ai = _automatedPlayers.get(player);
        if( ai == null ){
            _log.info("NO AI FOR " + player);
        }
        return ai;
    }
}
