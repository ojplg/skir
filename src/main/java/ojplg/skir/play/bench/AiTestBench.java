package ojplg.skir.play.bench;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.play.Channels;
import ojplg.skir.play.GamePurpose;
import ojplg.skir.play.GameRunner;
import ojplg.skir.play.NewGameRequest;
import ojplg.skir.state.GameId;
import ojplg.skir.state.Player;
import ojplg.skir.state.event.GameEventMessage;
import ojplg.skir.state.event.GameEventType;
import ojplg.skir.state.event.GameStartRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AiTestBench {

    private static final Logger _log = LogManager.getLogger(AiTestBench.class);

    private final boolean _useLateEliminationBonuses = false;

    private final Channels _channels;
    private final Fiber _fiber;
    private final int _gamesToRun;

    private final List<SimpleGameRecord> _gameRecords = new ArrayList<>();
    private final AiFactory _aiFactory;
    private final boolean _disposeOnComplete;

    private Consumer<GameScores> _resultsConsumer;
    private SimpleGameRecord _currentGameRecord;
    private GameRunner _gameRunner;

    public AiTestBench(AiFactory aiFactory, Channels channels, Fiber fiber, int gamesToRun, boolean disposeOnComplete){
        this._channels = channels;
        this._fiber = fiber;
        this._gamesToRun = gamesToRun;
        this._aiFactory = aiFactory;
        this._disposeOnComplete = disposeOnComplete;

        _channels.subscribeToAllGameEvents(fiber, this::handleGameEvent);
    }

    public void setAiToTest(Function<Player,AutomatedPlayer> playerGenerator){
        _aiFactory.setFirstPlayerFactory(playerGenerator);
    }

    public void setResultsConsumer(Consumer<GameScores> resultsConsumer){
        this._resultsConsumer = resultsConsumer;
    }

    public void start(){
        _fiber.start();
    }

    public void startRun(){
        GameId gameId = setUpNewGameRunner();
        _currentGameRecord = new SimpleGameRecord();
        _channels.publishGameStartRequest(new GameStartRequest(gameId, GamePurpose.AiTestBench));
    }

    private GameId setUpNewGameRunner(){
        if( _gameRunner != null){
            _gameRunner.stop();
        }
        NewGameRequest newGameRequest = NewGameRequest.aiTestBench();
        _gameRunner = new GameRunner(_aiFactory, _channels, newGameRequest);
        _gameRunner.start();
        return newGameRequest.getGameId();
    }

    private void handleGameEvent(GameEventMessage gameEvent){
        GameEventType gameEventType = gameEvent.getGameEventType();
        switch(gameEventType){
            case PlayerJoins:
                _currentGameRecord.playerJoined(gameEvent.getPlayerIdentifier());
                return;
            case PlayerEliminated:
                _currentGameRecord.playerEliminated(gameEvent.getPlayerIdentifier(), gameEvent.getTurnNumber());
                return;
            case Draw:
                _currentGameRecord.draw(gameEvent.getPlayerIdentifiers(), gameEvent.getTurnNumber());
                processGame();
                return;
            case Win:
                _currentGameRecord.win(gameEvent.getPlayerIdentifier(), gameEvent.getTurnNumber());
                processGame();
                return;
            default:
                return;
        }
    }

    private void processGame(){
        _log.info(_currentGameRecord.produceLogRecord());
        _gameRecords.add(_currentGameRecord);
        if( _gameRecords.size() < _gamesToRun){
            GameId gameId = setUpNewGameRunner();
            _currentGameRecord = new SimpleGameRecord();
            _log.info("Starting game " + gameId);
            _channels.publishGameStartRequest(new GameStartRequest(gameId, GamePurpose.AiTestBench));
        } else {
            GameScores scores = computeScores();
            _log.info("Scores \n" + scores);
            if( _resultsConsumer != null) {
                _resultsConsumer.accept(scores);
            }
            _gameRecords.clear();
            if(_disposeOnComplete){
                dispose();
            }
        }
    }

    public void dispose(){
        _fiber.dispose();
    }

    private GameScores computeScores(){
        return _gameRecords.stream()
                .map(r -> r.scoreGame(_useLateEliminationBonuses) )
                .reduce(new GameScores(), (s1, s2) -> s1.accumulate(s2));
    }
}
