package ojplg.skir.state.event;

public class ClientConnectedEvent {

    private final String _clientId;
    private final String _clientKey;

    public ClientConnectedEvent(String clientId, String clientKey) {
        this._clientId = clientId;
        this._clientKey = clientKey;
    }

    public String getClientId() {
        return _clientId;
    }
    public String getClientKey() {
        return _clientKey;
    }

    @Override
    public String toString() {
        return "ClientConnectedEvent{" +
                "_clientId='" + _clientId + '\'' +
                ", _clientKey='" + _clientKey + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientConnectedEvent that = (ClientConnectedEvent) o;

        if (!_clientId.equals(that._clientId)) return false;
        return _clientKey.equals(that._clientKey);

    }

    @Override
    public int hashCode() {
        int result = _clientId.hashCode();
        result = 31 * result + _clientKey.hashCode();
        return result;
    }
}
