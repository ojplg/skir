package ojplg.skir.state.event;

import org.json.simple.JSONObject;
import ojplg.skir.state.Player;

public class GameJoinedEvent {

    private final ClientConnectedEvent _clientConnectedEvent;
    private final Player _player;
    private boolean _firstPlayer;

    public GameJoinedEvent(ClientConnectedEvent clientConnectedEvent, Player player, boolean firstPlayer){
        this._clientConnectedEvent = clientConnectedEvent;
        this._player = player;
        this._firstPlayer = firstPlayer;
    }

    public String getClientKey() {
        return _clientConnectedEvent.getClientKey();
    }

    public Player getPlayer() {
        return _player;
    }

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "game_joined");
        jObject.put("client_key", getClientKey());
        jObject.put("color", getPlayer().getColor());
        jObject.put("first_player", _firstPlayer);
        return jObject;
    }

}
