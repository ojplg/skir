package ojplg.skir.play;

import ojplg.skir.state.Constants;

import java.time.LocalDateTime;

public class NewGameRequest {

    private final LocalDateTime _requestTime = LocalDateTime.now();
    private final String _requesterName;
    private final String _requesterAddress;
    private final String[] _aiNames;
    private final int _delay;
    private final GamePurpose _gamePurpose;

    public static NewGameRequest webDemo(String requesterName, String requesterAddress, String[] aiNames) {
        return new NewGameRequest(requesterName, requesterAddress, aiNames, Constants.WEB_PLAY_DELAY, GamePurpose.WebDemo);
    }

    public static NewGameRequest webPlay(String requesterName, String requesterAddress, String[] aiNames) {
        return new NewGameRequest(requesterName, requesterAddress, aiNames, Constants.WEB_PLAY_DELAY, GamePurpose.WebPlay);
    }

    public static NewGameRequest aiTestBench(){
        return new NewGameRequest("Local", "", Constants.AI_NAMES, 0, GamePurpose.AiTestBench);
    }

    public static NewGameRequest aiEvolution(){
        return new NewGameRequest("Local", "", Constants.AI_NAMES, 0, GamePurpose.AiEvolve);
    }

    private NewGameRequest(String requesterName, String requesterAddress, String[] aiNames, int delay, GamePurpose gamePurpose) {
        this._requesterName = requesterName;
        this._requesterAddress = requesterAddress;
        this._aiNames = aiNames;
        this._delay = delay;
        this._gamePurpose = gamePurpose;
    }

    public int getDelay() {
        return _delay;
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

    public String[] getAiNames() {
        return _aiNames;
    }

    public GamePurpose getGamePurpose() { return _gamePurpose; }
}
