package play;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import play.orders.Adjutant;
import play.orders.Order;
import play.orders.OrderType;
import state.event.ClientConnectedEvent;
import state.event.MapChangedEvent;
import state.event.PlayerChangedEvent;

public class Channels {

    public final Channel<MapChangedEvent> MapChangedEventChannel
            = new MemoryChannel<MapChangedEvent>();

    public final Channel<ClientConnectedEvent> ClientConnectedEventChannel
            = new MemoryChannel<ClientConnectedEvent>();

    public final Channel<PlayerChangedEvent> PlayerChangedEventChannel
            = new MemoryChannel<PlayerChangedEvent>();

    public final Channel<Order> OrderEnteredChannel
            = new MemoryChannel<Order>();

    public final Channel<OrderType> OrderTypeChannel
            = new MemoryChannel<OrderType>();

    public final Channel<Adjutant> AdjutantChannel
            = new MemoryChannel<Adjutant>();

}