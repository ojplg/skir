package ojplg.skir.web;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.play.Channels;
import ojplg.skir.play.GameRunner;
import ojplg.skir.play.NewGameRequest;
import ojplg.skir.state.GameId;
import ojplg.skir.state.GameState;
import ojplg.skir.state.event.GameEventMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;

import java.util.HashMap;
import java.util.Map;

public class WebRunner {

    private static final Logger _log = LogManager.getLogger(WebRunner.class);

    private final Map<GameId, GameRunner> _gameRunners = new HashMap<>();
    private final Channels _channels;
    private final Fiber _fiber;

    private final Object _lock = new Object();

    public WebRunner(Channels channels, Fiber fiber){
        _channels = channels;
        _fiber = fiber;
        _channels.subscribeToAllGameEvents(_fiber, this::handleGameEvent);
        _channels.subscribeToRestoreGameChannel(_fiber, this::handleRestoreGameRequest);
    }

    public void start(){
        _log.info("Starting");
        _fiber.start();
    }

    private void handleRestoreGameRequest(GameState gameState){
        GameRunner gameRunner = new GameRunner(_channels, gameState);
        synchronized (_lock){
            _gameRunners.put(gameState.getGameId(), gameRunner);
        }
        gameRunner.start();
    }

    GameId newGame(NewGameRequest request){
        synchronized (_lock) {
            _log.info("Creating new GameRunner");
            AiFactory aiFactory = new AiFactory(request.getAiNames());
            GameRunner gameRunner = new GameRunner(aiFactory,
                    _channels,
                    request
            );
            _gameRunners.put(gameRunner.getGameId(), gameRunner);
            gameRunner.start();
            return gameRunner.getGameId();
        }
    }

    Map<GameId, GameMenuEntry> getGameEntries(){
        synchronized (_lock) {
            Map<GameId, GameMenuEntry> map = new HashMap<>();
            for (GameRunner runner: _gameRunners.values()) {
                GameId id = runner.getGameId();
                NewGameRequest request = runner.getGameRequest();
                boolean started = runner.isStarted();
                GameMenuEntry entry = new GameMenuEntry(request, started);
                map.put(id, entry);
            }
            return map;
        }
    }

    private void handleGameEvent(GameEventMessage gameEvent){
        if (gameEvent.isGameOver()) {
            synchronized (_lock) {
                _log.info("Removing game runner for " + gameEvent.getGameId());
            GameRunner runner = _gameRunners.remove(gameEvent.getGameId());
            runner.stop();
            }
        }
    }
}
