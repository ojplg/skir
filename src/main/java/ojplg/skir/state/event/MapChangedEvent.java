package ojplg.skir.state.event;

import ojplg.skir.map.Country;
import org.json.simple.JSONObject;
import ojplg.skir.state.Player;

public class MapChangedEvent {

    private final Country _country;
    private final Player _player;
    private final int _armyCount;

    public MapChangedEvent(Country _country, Player _player, int _armyCount) {
        this._country = _country;
        this._player = _player;
        this._armyCount = _armyCount;
    }

    public Country getCountry() {
        return _country;
    }

    public Player getPlayer() {
        return _player;
    }

    public int getArmyCount() {
        return _armyCount;
    }

    public JSONObject toJson() {
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "map_update");
        jObject.put("country", _country.getName());
        jObject.put("color", _player.getColor());
        jObject.put("count", _armyCount);
        return jObject;
    }
}
