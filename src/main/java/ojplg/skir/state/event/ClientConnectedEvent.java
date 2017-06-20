package ojplg.skir.state.event;

public class ClientConnectedEvent {

    private final String _clientKey;
    private final String _displayName;
    private final String _address;
    private final boolean _demo;

    public ClientConnectedEvent(String clientKey, String displayName, String address, boolean demo) {
        this._clientKey = clientKey;
        this._displayName = displayName;
        this._address = address;
        this._demo = demo;
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

        return _clientKey.equals(that._clientKey);
    }

    @Override
    public int hashCode() {
        int result = _clientKey.hashCode();
        return result;
    }
}
