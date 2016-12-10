package ojplg.skir.state.event;

public class ClientConnectedEvent {

    private final int _clientId;
    private final String _clientKey;
    private final String _displayName;
    private final String _address;

    public ClientConnectedEvent(int clientId, String clientKey, String displayName, String address) {
        this._clientId = clientId;
        this._clientKey = clientKey;
        this._displayName = displayName;
        this._address = address;
    }

    public int getClientId() {
        return _clientId;
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

    @Override
    public String toString() {
        return "ClientConnectedEvent{" +
                "_clientId='" + _clientId + '\'' +
                ", _clientKey='" + _clientKey + '\'' +
                ", _displayName='" + _displayName + '\'' +
                ", _address='" + _address + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientConnectedEvent that = (ClientConnectedEvent) o;

        if (_clientId != that._clientId) return false;
        return _clientKey.equals(that._clientKey) &&
                _displayName.equals(that._displayName) &&
                _address.equals(that._address);
    }

    @Override
    public int hashCode() {
        int result = _clientId;
        result = 31 * result + _clientKey.hashCode();
        result = 31 * result + _displayName.hashCode();
        result = 31 * result + _address.hashCode();
        return result;
    }
}
