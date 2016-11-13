package state.event;

import card.Card;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import state.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerChangedEvent {
    private final Player _player;
    private final int _countryCount;
    private final int _armyCount;
    private final int _continentCount;
    private final List<Card> _cards;
    private final int _expectedGrant;

    public PlayerChangedEvent(Player player, int countryCount, int armyCount, List<Card> cards, int continentCount, int expectedGrant) {
        this._player = player;
        this._countryCount = countryCount;
        this._armyCount = armyCount;
        this._continentCount = continentCount;
        this._cards = Collections.unmodifiableList(new ArrayList<Card>(cards));
        this._expectedGrant = expectedGrant;
    }

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","player_update");
        jObject.put("color",_player.getColor().toLowerCase());
        jObject.put("armies", _armyCount);
        jObject.put("countries", _countryCount);
        jObject.put("card_count", _cards.size());
        jObject.put("continents", _continentCount);
        jObject.put("expected_armies", _expectedGrant);
        return jObject;
    }

    public JSONObject fullDetailsJson(){
        JSONObject jObject = toJson();
        jObject.put("cards", cardsToJson());
        return jObject;
    }

    private JSONArray cardsToJson(){
        JSONArray jArray = new JSONArray();

        for(Card card : _cards){
            JSONObject jObject = new JSONObject();
            jObject.put("country", card.getCountry());
            jObject.put("symbol", card.getType().toString());
            jArray.add(jObject);
        }

        return jArray;
    }
}
