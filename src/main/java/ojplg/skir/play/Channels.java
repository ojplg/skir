package ojplg.skir.play;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.GameId;
import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.GameSpecifiable;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import ojplg.skir.state.event.ClientConnectedEvent;
import ojplg.skir.state.event.GameJoinedEvent;
import ojplg.skir.state.event.MapChangedEvent;
import ojplg.skir.state.event.PlayerChangedEvent;
import org.jetlang.core.Callback;
import org.jetlang.core.DisposingExecutor;

public class Channels {

    public final Channel<MapChangedEvent> MapChangedEventChannel
            = new MemoryChannel<>();

    public final Channel<ClientConnectedEvent> ClientConnectedEventChannel
            = new MemoryChannel<>();

    public final Channel<PlayerChangedEvent> PlayerChangedEventChannel
            = new MemoryChannel<>();

    private final Channel<Order> OrderEnteredChannel
            = new MemoryChannel<>();

    public final Channel<Adjutant> AdjutantChannel
            = new MemoryChannel<>();

    public final Channel<GameJoinedEvent> GameJoinedEventChannel
            = new MemoryChannel<>();

    public final Channel<String> StartGameChannel
            = new MemoryChannel<>();

    public final Channel<GameEvent> GameEventChannel
            = new MemoryChannel<>();

    public void publishOrder(Order order){
        OrderEnteredChannel.publish(order);
    }

    public void subscribeToOrder(GameSpecifiable gameSpecifier, DisposingExecutor executor, Callback<Order> callback){
        OrderEnteredChannel.subscribe(executor,
                    order -> { if(order.matches(gameSpecifier)){ callback.onMessage(order); }});
    }
}
