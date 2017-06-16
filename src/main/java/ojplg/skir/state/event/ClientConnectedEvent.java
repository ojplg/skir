package ojplg.skir.state.event;

public class ClientConnectedEvent {

    private final String _clientKey;
    private final String _displayName;
    private final String _address;

    public ClientConnectedEvent(String clientKey, String displayName, String address) {
        this._clientKey = clientKey;
        this._displayName = displayName;
        this._address = address;
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

        return _clientKey.equals(that._clientKey) &&
                _displayName.equals(that._displayName) &&
                _address.equals(that._address);
    }

    @Override
    public int hashCode() {
        int result = _clientKey.hashCode();
        result = 31 * result + _displayName.hashCode();
        result = 31 * result + _address.hashCode();
        return result;
    }
}
