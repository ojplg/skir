package state.event;

import org.json.simple.JSONObject;
import state.Player;

public class PlayerChangedEvent {
    private final Player _player;
    private final int _countryCount;
    private final int _armyCount;
    private final int _cardCount;
    private final int _continentCount;

    public PlayerChangedEvent(Player player, int countryCount, int armyCount, int cardCount, int continentCount) {
        this._player = player;
        this._countryCount = countryCount;
        this._armyCount = armyCount;
        this._cardCount = cardCount;
        this._continentCount = continentCount;
    }

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","player_update");
        jObject.put("color",_player.getColor().toLowerCase());
        jObject.put("armies", _armyCount);
        jObject.put("countries", _countryCount);
        jObject.put("card_count", _cardCount);
        jObject.put("continents", _continentCount);
        return jObject;
    }
}
