package ojplg.skir.state.event;

import ojplg.skir.card.Card;
import ojplg.skir.state.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
    private final double _attackLuckFactor;
    private final double _defenseLuckFactor;

    public PlayerChangedEvent(Player player, int countryCount, int armyCount, int continentCount, int expectedGrant) {
        this._player = player;
        this._countryCount = countryCount;
        this._armyCount = armyCount;
        this._continentCount = continentCount;
        this._cards = Collections.unmodifiableList(new ArrayList<Card>(player.getCards()));
        this._expectedGrant = expectedGrant;
        this._attackLuckFactor = player.attackLuckFactor();
        this._defenseLuckFactor = player.defenseLuckFactor();
    }

    public String getClientKey(){
        return _player.getClientKey();
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
        jObject.put("attack_luck_factor", lowPrecisionDouble(_attackLuckFactor));
        jObject.put("defense_luck_factor", lowPrecisionDouble(_defenseLuckFactor));
        return jObject;
    }

    private String lowPrecisionDouble(double value){
        return String.format("%.2f",value);
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
            if ( card.isJoker() ){
                jObject.put("joker", "true");
            } else {
                jObject.put("country", card.getCountry().getName());
                jObject.put("symbol", card.getType().toString());
            }
            jArray.add(jObject);
        }

        return jArray;
    }
}
