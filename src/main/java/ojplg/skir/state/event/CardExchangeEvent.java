package ojplg.skir.state.event;

import ojplg.skir.state.GameId;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

public class CardExchangeEvent implements GameEventMessage {

    private final GameId _gameId;
    private final int _turnNumber;
    private final String _playerIdentifier;
    private final int _nextCardExchangeValue;

    public CardExchangeEvent(GameId gameId, int turnNumber, String playerIdentifier, int nextCardExchangeValue){
        this._gameId = gameId;
        this._turnNumber = turnNumber;
        this._playerIdentifier = playerIdentifier;
        this._nextCardExchangeValue = nextCardExchangeValue;
    }

    @Override
    public GameId getGameId() {
        return _gameId;
    }

    @Override
    public int getTurnNumber() {
        return _turnNumber;
    }

    @Override
    public GameEventType getGameEventType() {
        return GameEventType.ExchangeCards;
    }

    @Override
    public String getPlayerIdentifier() {
        return _playerIdentifier;
    }

    @Override
    public List<String> getPlayerIdentifiers() {
        return Collections.singletonList(_playerIdentifier);
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
        jObject.put("simple_text", simpleText());
        jObject.put("turn_number", _turnNumber);
        jObject.put("game_over", false);
        jObject.put("next_card_exchange_value", _nextCardExchangeValue);
        return jObject;
    }

    private String simpleText(){
        StringBuilder buf = new StringBuilder();
        buf.append(_playerIdentifier);
        buf.append(" exchanges cards");
        return buf.toString();
    }
}
