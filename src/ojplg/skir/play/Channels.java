package ojplg.skir.play;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.event.GameEvent;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import ojplg.skir.state.event.ClientConnectedEvent;
import ojplg.skir.state.event.GameJoinedEvent;
import ojplg.skir.state.event.MapChangedEvent;
import ojplg.skir.state.event.PlayerChangedEvent;

public class Channels {

    public final Channel<MapChangedEvent> MapChangedEventChannel
            = new MemoryChannel<>();

    public final Channel<ClientConnectedEvent> ClientConnectedEventChannel
            = new MemoryChannel<>();

    public final Channel<PlayerChangedEvent> PlayerChangedEventChannel
            = new MemoryChannel<>();

    public final Channel<Order> OrderEnteredChannel
            = new MemoryChannel<>();

    public final Channel<Adjutant> AdjutantChannel
            = new MemoryChannel<>();

    public final Channel<GameJoinedEvent> GameJoinedEventChannel
            = new MemoryChannel<>();

    public final Channel<String> StartGameChannel
            = new MemoryChannel<>();

    public final Channel<GameEvent> GameEventChannel
            = new MemoryChannel<>();
}
