package ojplg.skir.state.event;

import ojplg.skir.state.GameId;

public class NoMoveReceivedEvent implements GameSpecifiable {

    private final GameId _gameId;

    public NoMoveReceivedEvent(GameId gameId) {
        this._gameId = gameId;
    }

    @Override
    public GameId getGameId() {
        return _gameId;
    }
}
