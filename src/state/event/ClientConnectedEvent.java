package state.event;

public class ClientConnectedEvent {

    private final String _clientId;

    public ClientConnectedEvent(String _clientId) {
        this._clientId = _clientId;
    }

    public String getClientId() {
        return _clientId;
    }
}
