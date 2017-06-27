package ojplg.skir.play;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
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

    private final Channel<Order> _orderMemoryChannel = new MemoryChannel<>();
    private final Channel<Adjutant> _adjutantChannel = new MemoryChannel<>();
    private final Channel<MapChangedEvent> _mapChangedEventChannel = new MemoryChannel<>();
    private final Channel<PlayerChangedEvent> _playerChangedEventChannel = new MemoryChannel<>();
    private final Channel<GameEvent> _gameEventChannel = new MemoryChannel<>();
    private final Channel<GameJoinedEvent> _gameJoinedEventChannel = new MemoryChannel<>();
    private final Channel<ClientConnectedEvent> _clientConnectedEventChannel = new MemoryChannel<>();

    public final Channel<String> StartGameChannel
            = new MemoryChannel<>();

    public void publishOrder(Order order){
        _orderMemoryChannel.publish(order);
    }

    public void subscribeToOrder(GameSpecifiable gameSpecifier, DisposingExecutor executor, Callback<Order> callback){
        gameSpecifiedSubscription(gameSpecifier, executor, callback, _orderMemoryChannel);
    }

    public void publishAdjutant(Adjutant adjutant){
        _adjutantChannel.publish(adjutant);
    }

    public void subscribeToAdjutant(GameSpecifiable gameSpecifier, DisposingExecutor executor, Callback<Adjutant> callback){
        gameSpecifiedSubscription(gameSpecifier, executor, callback, _adjutantChannel);
    }

    public void publishMapChangedEvent(MapChangedEvent mapChangedEvent){
        _mapChangedEventChannel.publish(mapChangedEvent);
    }

    public void subscribeToMapChangedEvent(GameSpecifiable gameSpecifier, DisposingExecutor executor, Callback<MapChangedEvent> callback){
        gameSpecifiedSubscription(gameSpecifier, executor, callback, _mapChangedEventChannel);
    }

    public void publishPlayerChangedEvent(PlayerChangedEvent event){
        _playerChangedEventChannel.publish(event);
    }

    public void subscribeToPlayerChangedEvent(GameSpecifiable gameSpecifier, DisposingExecutor executor, Callback<PlayerChangedEvent> callback){
        gameSpecifiedSubscription(gameSpecifier, executor, callback, _playerChangedEventChannel);
    }

    public void publishGameEvent(GameEvent event){
        _gameEventChannel.publish(event);
    }

    public void subscribeToGameEvent(GameSpecifiable gameSpecifier, DisposingExecutor executor, Callback<GameEvent> callback){
        gameSpecifiedSubscription(gameSpecifier, executor, callback, _gameEventChannel);
    }

    public void subscribeToAllGameEvents(DisposingExecutor executor, Callback<GameEvent> callback){
        _gameEventChannel.subscribe(executor, callback);
    }

    public void publishGameJoinedEvent(GameJoinedEvent event){
        _gameJoinedEventChannel.publish(event);
    }

    public void subscribeToGameJoinedEvent(GameSpecifiable gameSpecifier, DisposingExecutor executor, Callback<GameJoinedEvent> callback){
        gameSpecifiedSubscription(gameSpecifier, executor, callback, _gameJoinedEventChannel);
    }

    public void publishClientConnectedEvent(ClientConnectedEvent event) { _clientConnectedEventChannel.publish(event);}

    public void subscribeToClientConnectedEvent(GameSpecifiable gameSpecifiable, DisposingExecutor executor,
                                                Callback<ClientConnectedEvent> callback){
        gameSpecifiedSubscription(gameSpecifiable, executor, callback, _clientConnectedEventChannel);
    }

    private <T extends GameSpecifiable> void gameSpecifiedSubscription(GameSpecifiable gameSpecifier, DisposingExecutor executor,
                                                                       Callback<T> callback, Channel<T> channel){
        channel.subscribe(executor,
                    t ->  { if(t.matches(gameSpecifier)){ callback.onMessage(t); }});

    }
}
