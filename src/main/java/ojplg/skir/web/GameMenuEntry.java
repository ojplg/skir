package ojplg.skir.web;

import ojplg.skir.play.GamePurpose;
import ojplg.skir.play.NewGameRequest;

import java.time.LocalDateTime;

public class GameMenuEntry {

    private final LocalDateTime _requestTime;
    private final String _requesterName;
    private final String _requesterAddress;
    private final GamePurpose _gamePurpose;
    private final boolean _started;
    private final boolean _restored;

    public GameMenuEntry(NewGameRequest gameRequest, boolean started) {
        _requestTime = gameRequest.getRequestTime();
        _requesterName = gameRequest.getRequesterName();
        _requesterAddress = gameRequest.getRequesterAddress();
        _gamePurpose = gameRequest.getGamePurpose();
        this._started = started;
        this._restored =  gameRequest.isRestored();
    }

    public boolean isJoinable(){
        if ( GamePurpose.WebPlay.equals(_gamePurpose)){
            if( _restored ){
                return true;
            }
            if ( !_started ){
                return true;
            }
        }
        return false;
    }

    public boolean isDemo(){
        return getGamePurpose().equals(GamePurpose.WebDemo);
    }

    public boolean isStarted(){
        return _started;
    }

    public LocalDateTime getRequestTime() {
        return _requestTime;
    }

    public String getRequesterName() {
        return _requesterName;
    }

    public String getRequesterAddress() {
        return _requesterAddress;
    }

    public GamePurpose getGamePurpose() { return _gamePurpose; }

}
