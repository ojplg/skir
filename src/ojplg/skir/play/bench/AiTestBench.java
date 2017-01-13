package ojplg.skir.play.bench;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.play.Channels;
import ojplg.skir.state.Player;
import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.GameEventType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AiTestBench {

    private static final Logger _log = LogManager.getLogger(AiTestBench.class);

    private final Channels _channels;
    private final Fiber _fiber;
    private final int _gamesToRun;

    private final List<SimpleGameRecord> _gameRecords = new ArrayList<>();
    private final AiFactory _aiFactory;

    private SimpleGameRecord _currentGameRecord;

    public AiTestBench(AiFactory aiFactory, Channels channels, Fiber fiber, int gamesToRun){
        this._channels = channels;
        this._fiber = fiber;
        this._gamesToRun = gamesToRun;
        this._aiFactory = aiFactory;

        _channels.GameEventChannel.subscribe(fiber, this::handleGameEvent);
    }

    public void setAiToTest(Function<Player,AutomatedPlayer> playerGenerator){
        _aiFactory.setFirstPlayerFactory(playerGenerator);
    }

    public void start(){
        _fiber.start();
        _currentGameRecord = new SimpleGameRecord();
        _channels.InitializeGameChannel.publish("Test bench initializing");
        _channels.StartGameChannel.publish("Test bench starting");
    }

    private void handleGameEvent(GameEvent gameEvent){
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
        _gameRecords.add(_currentGameRecord);
        if( _gameRecords.size() < _gamesToRun){
            _currentGameRecord = new SimpleGameRecord();
            _log.info("Starting game " + _gameRecords.size() + 1);
            _channels.InitializeGameChannel.publish("TestBenchInitialize");
            _channels.StartGameChannel.publish("TestBenchGame " + _gameRecords.size() + 1);
        } else {
            _gameRecords.forEach( gr -> _log.info(gr.produceLogRecord()));
            _log.info("Scores\n" + computeScores());
        }
    }

    private GameScores computeScores(){
        return _gameRecords.stream()
                .map(r -> r.scoreGame() )
                .reduce(new GameScores(), (s1, s2) -> s1.accumulate(s2));
    }
}
