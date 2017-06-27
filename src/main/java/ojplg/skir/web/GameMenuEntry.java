package ojplg.skir.web;

import ojplg.skir.play.GamePurpose;
import ojplg.skir.play.NewGameRequest;

import java.time.LocalDateTime;

public class GameMenuEntry {

    private final NewGameRequest _gameRequest;
    private final boolean _started;

    public GameMenuEntry(NewGameRequest gameRequest, boolean started) {
        this._gameRequest = gameRequest;
        this._started = started;
    }

    public boolean isJoinable(){
        return (!_started) && getGamePurpose().equals(GamePurpose.WebPlay);
    }

    public boolean isStarted(){
        return _started;
    }

    public LocalDateTime getRequestTime() {
        return _gameRequest.getRequestTime();
    }

    public String getRequesterName() {
        return _gameRequest.getRequesterName();
    }

    public String getRequesterAddress() {
        return _gameRequest.getRequesterAddress();
    }

    public GamePurpose getGamePurpose() { return _gameRequest.getGamePurpose(); }

}
