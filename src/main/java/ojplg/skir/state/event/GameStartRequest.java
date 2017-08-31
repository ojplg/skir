package ojplg.skir.state.event;

import ojplg.skir.play.GamePurpose;
import ojplg.skir.state.GameId;

public class GameStartRequest implements GameSpecifiable {

    private final GameId _gameId;
    private final GamePurpose _gamePurpose;

    public GameStartRequest(GameId gameId, GamePurpose gamePurpose){
        _gameId = gameId;
        _gamePurpose = gamePurpose;
    }

    @Override
    public GameId getGameId() {
        return _gameId;
    }

    @Override
    public boolean matches(GameSpecifiable other) {
        return _gameId.equals(other.getGameId());
    }

    public GamePurpose getGamePurpose() {
        return _gamePurpose;
    }

    @Override
    public String toString() {
        return "GameStartRequest { _gameId=" + _gameId + ", _gamePurpose=" + _gamePurpose + "}";
    }
}
