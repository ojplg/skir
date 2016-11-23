package ojplg.skir.state.event;

import org.json.simple.JSONObject;
import ojplg.skir.state.Player;

public class GameJoinedEvent {

    private final String _clientKey;
    private final Player _player;
    private boolean _firstPlayer;

    public GameJoinedEvent(String clientKey, Player player, boolean firstPlayer) {
        this._clientKey = clientKey;
        this._player = player;
        this._firstPlayer = firstPlayer;
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
        jObject.put("first_color", _firstPlayer);
        return jObject;
    }

}
