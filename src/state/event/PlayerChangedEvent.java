package state.event;

import org.json.simple.JSONObject;
import state.Player;

public class PlayerChangedEvent {
    private final Player _player;
    private final int _countryCount;
    private final int _armyCount;

    public PlayerChangedEvent(Player _player, int _countryCount, int _armyCount) {
        this._player = _player;
        this._countryCount = _countryCount;
        this._armyCount = _armyCount;
    }

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","player_update");
        jObject.put("color",_player.getColor().toLowerCase());
        jObject.put("armies", _armyCount);
        jObject.put("countries", _countryCount);
        return jObject;
    }
}
