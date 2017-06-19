package ojplg.skir.state.event;

import ojplg.skir.map.Country;
import ojplg.skir.state.GameId;
import org.json.simple.JSONObject;
import ojplg.skir.state.Player;

public class MapChangedEvent implements GameSpecifiable {

    private final Country _country;
    private final Player _player;
    private final int _armyCount;
    private final GameId _gameId;

    public MapChangedEvent(GameId gameId, Country _country, Player _player, int _armyCount) {
        this._gameId = gameId;
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

    @Override
    public GameId getGameId() {
        return _gameId;
    }

    public JSONObject toJson() {
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "map_update");
        jObject.put("country", _country.getName());
        jObject.put("color", _player.getColor());
        jObject.put("count", _armyCount);
        jObject.put("game_id", _gameId.getId());
        return jObject;
    }
}
