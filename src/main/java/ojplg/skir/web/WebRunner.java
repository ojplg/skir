package ojplg.skir.web;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.play.Channels;
import ojplg.skir.play.GameRunner;
import ojplg.skir.play.Skir;
import ojplg.skir.state.Constants;
import ojplg.skir.state.GameId;

import java.util.HashMap;
import java.util.Map;

public class WebRunner {

    private final Map<GameId, GameRunner> _gameRunners = new HashMap<>();
    private final Channels _channels;

    public WebRunner(Channels channels){
        _channels = channels;
    }

    public void newGame(String[] aiNames){
        AiFactory aiFactory = new AiFactory(aiNames);
        GameRunner gameRunner = new GameRunner(aiFactory,
                _channels,
                Skir.createThreadFiber("WebGameRunner"),
                Constants.WEB_PLAY_DELAY
                );
        _gameRunners.put(gameRunner.getGameId(), gameRunner);
        gameRunner.start();
    }
}
