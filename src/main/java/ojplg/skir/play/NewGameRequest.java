package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.state.Constants;
import ojplg.skir.state.GameId;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class NewGameRequest {

    private final LocalDateTime _requestTime = LocalDateTime.now();
    private final String _requesterName;
    private final String _requesterAddress;
    private final List<String> _aiNames;
    private final int _delay;
    private final GamePurpose _gamePurpose;
    private final GameId _gameId;

    public static NewGameRequest webDemo(String requesterName, String requesterAddress, List<String> aiNames) {
        return new NewGameRequest(requesterName, requesterAddress, aiNames, Constants.WEB_PLAY_DELAY, GamePurpose.WebDemo);
    }

    public static NewGameRequest webPlay(String requesterName, String requesterAddress, List<String> aiNames) {
        return new NewGameRequest(requesterName, requesterAddress, aiNames, Constants.WEB_PLAY_DELAY, GamePurpose.WebPlay);
    }

    public static NewGameRequest aiTestBench(){
        return new NewGameRequest("Local", "", AiFactory.allPlayerNames(), 0, GamePurpose.AiTestBench);
    }

    public static NewGameRequest aiEvolution(){
        return new NewGameRequest("Local", "", AiFactory.allPlayerNames(), 0, GamePurpose.AiEvolve);
    }

    public static NewGameRequest restoreGame(GameId gameId){
        return new NewGameRequest(gameId);
    }

    private NewGameRequest(GameId gameId){
        this._requesterName = "Restored";
        this._requesterAddress = "localhost";
        this._aiNames = Collections.emptyList();
        this._delay = 0;
        this._gamePurpose = GamePurpose.WebPlay;
        this._gameId = gameId;
    }

    private NewGameRequest(String requesterName, String requesterAddress, List<String> aiNames, int delay, GamePurpose gamePurpose) {
        this._requesterName = requesterName;
        this._requesterAddress = requesterAddress;
        this._aiNames = aiNames;
        this._delay = delay;
        this._gamePurpose = gamePurpose;
        this._gameId = GameId.next();
    }

    int getDelay() {
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

    public List<String> getAiNames() {
        return _aiNames;
    }

    public GamePurpose getGamePurpose() { return _gamePurpose; }

    public GameId getGameId(){
        return _gameId;
    }
}
