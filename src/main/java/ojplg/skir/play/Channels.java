package ojplg.skir.play;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.GameState;
import ojplg.skir.state.event.GameEventMessage;
import ojplg.skir.state.event.GameSpecifiable;
import ojplg.skir.state.event.GameStartRequest;
import ojplg.skir.state.event.NoMoveReceivedEvent;
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
    private final Channel<GameEventMessage> _gameEventChannel = new MemoryChannel<>();
    private final Channel<GameJoinedEvent> _gameJoinedEventChannel = new MemoryChannel<>();
    private final Channel<ClientConnectedEvent> _clientConnectedEventChannel = new MemoryChannel<>();
    private final Channel<GameStartRequest> _gameStartRequestChannel = new MemoryChannel<>();
    private final Channel<NoMoveReceivedEvent> _noMoveReceivedEventChannel = new MemoryChannel<>();
    private final Channel<GameState> _restoreGamesChannel = new MemoryChannel<>();

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

    public void publishGameEvent(GameEventMessage event){
        _gameEventChannel.publish(event);
    }

    public void subscribeToGameEvent(GameSpecifiable gameSpecifier, DisposingExecutor executor, Callback<GameEventMessage> callback){
        gameSpecifiedSubscription(gameSpecifier, executor, callback, _gameEventChannel);
    }

    public void subscribeToAllGameEvents(DisposingExecutor executor, Callback<GameEventMessage> callback){
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

    public void publishGameStartRequest(GameStartRequest gameStartRequest){
        _gameStartRequestChannel.publish(gameStartRequest);
    }

    public void subscribeToGameStartRequest(GameSpecifiable gameSpecifiable, DisposingExecutor executor,
                                            Callback<GameStartRequest> callback){
        gameSpecifiedSubscription(gameSpecifiable, executor, callback, _gameStartRequestChannel);
    }

    private <T extends GameSpecifiable> void gameSpecifiedSubscription(GameSpecifiable gameSpecifier, DisposingExecutor executor,
                                                                       Callback<T> callback, Channel<T> channel){
        channel.subscribe(executor,
                    t ->  { if(t.matches(gameSpecifier)){ callback.onMessage(t); }});

    }

    public void publishNoMoveReceivedEvent(NoMoveReceivedEvent event){
        _noMoveReceivedEventChannel.publish(event);
    }

    public void subscribeToNoMoveReceviedEvent(GameSpecifiable gameSpecifiable,
                                               DisposingExecutor executor,
                                               Callback<NoMoveReceivedEvent> callback){
        gameSpecifiedSubscription(gameSpecifiable, executor, callback, _noMoveReceivedEventChannel);
    }

    public void publishRestoreGame(GameState gameState){
        _restoreGamesChannel.publish(gameState);
    }

    public void subscribeToRestoreGameChannel(DisposingExecutor executor, Callback<GameState> callback){
        _restoreGamesChannel.subscribe(executor, callback);
    }
}
