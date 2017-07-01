package ojplg.skir.state.event;

import ojplg.skir.state.GameId;

public class ClientConnectedEvent implements GameSpecifiable {

    private final String _clientKey;
    private final String _displayName;
    private final String _address;
    private final boolean _demo;
    private final GameId _gameId;
    private final boolean _joinAttempt;

    public ClientConnectedEvent(String clientKey, String displayName, String address, boolean demo, GameId gameId, boolean joinAttempt) {
        this._clientKey = clientKey;
        this._displayName = displayName;
        this._address = address;
        this._demo = demo;
        this._gameId = gameId;
        this._joinAttempt = joinAttempt;
    }

    public String getClientKey() {
        return _clientKey;
    }

    public String getDisplayName() {
        return _displayName;
    }

    public String getAddress() {
        return _address;
    }

    public boolean isDemo() { return _demo; }

    public boolean isJoinAttempt() {
        return _joinAttempt;
    }

    public GameId getGameId() { return _gameId; }

    @Override
    public String toString() {
        return "ClientConnectedEvent{" +
                "_clientKey='" + _clientKey + '\'' +
                ", _displayName='" + _displayName + '\'' +
                ", _address='" + _address + '\'' +
                ", _demo='" + _demo + '\'' +
                ", _gameId='" + _gameId + '\'' +
                ", _joinAttempt='" + _joinAttempt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientConnectedEvent that = (ClientConnectedEvent) o;

        return _clientKey.equals(that._clientKey);
    }

    @Override
    public int hashCode() {
        int result = _clientKey.hashCode();
        return result;
    }
}
