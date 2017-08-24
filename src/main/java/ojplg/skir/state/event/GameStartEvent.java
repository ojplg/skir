package ojplg.skir.state.event;

import ojplg.skir.state.GameId;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

public class GameStartEvent implements GameEventMessage {

    private final GameId _gameId;
    private final int _nextCardExchangeValue;

    public GameStartEvent(GameId gameId, int nextCardExchangeValue){
        this._gameId = gameId;
        this._nextCardExchangeValue = nextCardExchangeValue;
    }

    @Override
    public GameId getGameId() {
        return _gameId;
    }

    @Override
    public int getTurnNumber() {
        return 0;
    }

    @Override
    public GameEventType getGameEventType() {
        return GameEventType.Start;
    }

    @Override
    public String getPlayerIdentifier() {
        return null;
    }

    @Override
    public List<String> getPlayerIdentifiers() {
        return Collections.emptyList();
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "game_event");
        jObject.put("game_id", _gameId.getId());
        jObject.put("simple_text", "Game started");
        jObject.put("turn_number", 0);
        jObject.put("game_over", false);
        jObject.put("next_card_exchange_value", _nextCardExchangeValue);
        return jObject;

    }
}
