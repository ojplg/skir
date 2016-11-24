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
}
