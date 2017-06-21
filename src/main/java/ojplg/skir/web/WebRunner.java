package ojplg.skir.web;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.play.Channels;
import ojplg.skir.play.GameRunner;
import ojplg.skir.play.Skir;
import ojplg.skir.state.Constants;
import ojplg.skir.state.GameId;
import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.GameEventType;
import org.jetlang.fibers.Fiber;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebRunner {

    private final Map<GameId, GameRunner> _gameRunners = new HashMap<>();
    private final Channels _channels;
    private final Fiber _fiber;

    public WebRunner(Channels channels){
        _channels = channels;
        _fiber = Skir.createThreadFiber("WebRunner");
        channels.subscribeToAllGameEvents(_fiber, this::handleGameEvent);
    }

    public void start(){
        _fiber.start();
    }

    public GameId newGame(String[] aiNames){
        AiFactory aiFactory = new AiFactory(aiNames);
        GameRunner gameRunner = new GameRunner(aiFactory,
                _channels,
                Skir.createThreadFiber("WebGameRunner"),
                Constants.WEB_PLAY_DELAY
                );
        _gameRunners.put(gameRunner.getGameId(), gameRunner);
        gameRunner.start();
        return gameRunner.getGameId();
    }

    public Set<GameId> getGameIds(){
        return _gameRunners.keySet();
    }

    private void handleGameEvent(GameEvent gameEvent){
        if(gameEvent.isGameOver()){
            _gameRunners.remove(gameEvent.getGameId());
        }
    }
}
