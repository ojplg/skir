package ojplg.skir.state.event;

import ojplg.skir.state.GameId;
import org.json.simple.JSONObject;
import ojplg.skir.state.Player;

public class GameJoinedEvent implements GameSpecifiable {

    private final ClientConnectedEvent _clientConnectedEvent;
    private final Player _player;
    private boolean _firstPlayer;
    private final GameId _gameId;

    public GameJoinedEvent(ClientConnectedEvent clientConnectedEvent, Player player, boolean firstPlayer, GameId gameId){
        this._clientConnectedEvent = clientConnectedEvent;
        this._player = player;
        this._firstPlayer = firstPlayer;
        this._gameId = gameId;
    }

    public String getClientKey() {
        return _clientConnectedEvent.getClientKey();
    }

    public Player getPlayer() {
        return _player;
    }

    public GameId getGameId(){
        return _gameId;
    }

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "game_joined");
        jObject.put("client_key", getClientKey());
        jObject.put("color", getPlayer().getColor());
        jObject.put("first_player", _firstPlayer);
        return jObject;
    }

    public String toString(){
        return toJson().toString();
    }
}
