package ojplg.skir.play;

import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.GameEventType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;

import java.util.ArrayList;
import java.util.List;

public class AiTestBench {

    private static final Logger _log = LogManager.getLogger(AiTestBench.class);

    private final Channels _channels;
    private final Fiber _fiber;
    private final int _gamesToRun;

    private final List<SimpleGameRecord> _gameRecords = new ArrayList<>();

    private SimpleGameRecord _currentGameRecord;

    public AiTestBench(Channels channels, Fiber fiber, int gamesToRun){
        this._channels = channels;
        this._fiber = fiber;
        this._gamesToRun = gamesToRun;

        _channels.GameEventChannel.subscribe(fiber, this::handleGameEvent);
    }

    public void start(){
        _fiber.start();
        _currentGameRecord = new SimpleGameRecord();
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
                _currentGameRecord.draw(gameEvent.getTurnNumber());
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
            _channels.StartGameChannel.publish("TestBenchGame " + _gameRecords.size() + 1);
        } else {
            _gameRecords.forEach( gr -> _log.info(gr.produceLogRecord()));
        }
    }
}
