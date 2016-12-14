package ojplg.skir.state;

import ojplg.skir.state.event.ClientConnectedEvent;

public class ClientInfo {

    private String _clientAddress;
    private final int _clientId;
    private final String _color;
    private final String _clientKey;

    public ClientInfo(ClientConnectedEvent clientConnectedEvent, Player player){
        this._clientAddress = "";
        this._clientId = clientConnectedEvent.getClientId();
        this._clientKey = clientConnectedEvent.getClientKey();
        this._color = player.getColor();
    }

    public String getClientAddress() {
        return _clientAddress;
    }

    public int getClientId() {
        return _clientId;
    }

    public String getColor() {
        return _color;
    }

    public String getClientKey() {
        return _clientKey;
    }
}
