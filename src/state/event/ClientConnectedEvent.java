package state.event;

public class ClientConnectedEvent {

    private final String _clientId;
    private final String _clientAddress;

    public ClientConnectedEvent(String clientId , String clientAddress) {
        this._clientId = clientId;
        this._clientAddress = clientAddress;
    }

    public String getClientId() {
        return _clientId;
    }
    public String getClientAddress() {
        return _clientAddress;
    }
}
