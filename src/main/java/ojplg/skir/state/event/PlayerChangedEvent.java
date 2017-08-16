package ojplg.skir.state.event;

import ojplg.skir.card.Card;
import ojplg.skir.state.BattleStats;
import ojplg.skir.state.GameId;
import ojplg.skir.state.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerChangedEvent implements GameSpecifiable {
    private final Player _player;
    private final int _countryCount;
    private final int _armyCount;
    private final int _continentCount;
    private final List<Card> _cards;
    private final int _expectedGrant;
    private final BattleStats _battleStats;
    private final GameId _gameId;

    public PlayerChangedEvent(GameId gameId, Player player, BattleStats battleStats, List<Card> cards, int countryCount, int armyCount, int continentCount, int expectedGrant) {
        this._player = player;
        this._battleStats = battleStats;
        this._countryCount = countryCount;
        this._armyCount = armyCount;
        this._continentCount = continentCount;
        this._cards = Collections.unmodifiableList(new ArrayList<Card>(cards));
        this._expectedGrant = expectedGrant;
        this._gameId = gameId;
    }

    public String getClientKey(){
        return _player.getClientKey();
    }

    public GameId getGameId() {
        return _gameId;
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
        jObject.put("name", _player.getDisplayName());
        jObject.put("attack_luck_factor", lowPrecisionDouble(_battleStats.getAttackLuckFactor()));
        jObject.put("defense_luck_factor", lowPrecisionDouble(_battleStats.getDefenseLuckFactor()));
        jObject.put("armies_lost_attacking", _battleStats.getTotalArmiesLostAttacking());
        jObject.put("armies_killed_attacking", _battleStats.getTotalArmiesLostAttacking());
        jObject.put("armies_lost_defending", _battleStats.getTotalArmiesLostDefending());
        jObject.put("armies_killed_defending", _battleStats.getTotalArmiesKilledDefending());
        jObject.put("game_id" , _gameId.getId());

        return jObject;
    }

    private String lowPrecisionDouble(double value){
        return String.format("%.2f",value);
    }

    @Override
    public String toString() {
        return toJson().toString();
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
