package state.event;

import state.Player;

public class GameJoinedEvent {

    private final String _clientKey;
    private final Player _player;

    public GameJoinedEvent(String clientKey, Player player) {
        this._clientKey = clientKey;
        this._player = player;
    }

    public String getClientKey() {
        return _clientKey;
    }

    public Player getPlayer() {
        return _player;
    }
}
