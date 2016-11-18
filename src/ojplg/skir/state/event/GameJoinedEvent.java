package ojplg.skir.state.event;

import org.json.simple.JSONObject;
import ojplg.skir.state.Player;

public class GameJoinedEvent {

    private final String _clientKey;
    private final Player _player;

    public GameJoinedEvent(String clientKey, Player player) {
        this._clientKey = clientKey;
        this._player = player;
    }

    public String getClientKey() {
        return _clientKey;
    }

    public Player getPlayer() {
        return _player;
    }

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "player_joined");
        jObject.put("client_key", getClientKey());
        jObject.put("color", getPlayer().getColor());
        return jObject;
    }

}
