package ojplg.skir.state.event;

public class JoinGameRequest {

    private final String _name;
    private final String _remoteAddress;

    public JoinGameRequest(String name, String remoteAddress) {
        this._name = name;
        this._remoteAddress = remoteAddress;
    }

    public String getName() {
        return _name;
    }

    public String getRemoteAddress() {
        return _remoteAddress;
    }

    @Override
    public String toString() {
        return "JoinGameRequest{" +
                "_name='" + _name + '\'' +
                ", _remoteAddress='" + _remoteAddress + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinGameRequest that = (JoinGameRequest) o;

        if (_name != null ? !_name.equals(that._name) : that._name != null) return false;
        return _remoteAddress != null ? _remoteAddress.equals(that._remoteAddress) : that._remoteAddress == null;

    }

    @Override
    public int hashCode() {
        int result = _name != null ? _name.hashCode() : 0;
        result = 31 * result + (_remoteAddress != null ? _remoteAddress.hashCode() : 0);
        return result;
    }
}
