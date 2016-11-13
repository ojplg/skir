package state.event;

public class ClientConnectedEvent {

    private final String _clientId;
    private final String _clientAddress;
    private final String _clientKey;

    public ClientConnectedEvent(String clientId , String clientAddress, String clientKey) {
        this._clientId = clientId;
        this._clientAddress = clientAddress;
        this._clientKey = clientKey;
    }

    public String getClientId() {
        return _clientId;
    }
    public String getClientAddress() {
        return _clientAddress;
    }
    public String getClientKey() {
        return _clientKey;
    }

    @Override
    public String toString() {
        return "ClientConnectedEvent{" +
                "_clientId='" + _clientId + '\'' +
                ", _clientAddress='" + _clientAddress + '\'' +
                ", _clientKey='" + _clientKey + '\'' +
                '}';
    }
}
