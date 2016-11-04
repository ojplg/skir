package play;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
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

}
