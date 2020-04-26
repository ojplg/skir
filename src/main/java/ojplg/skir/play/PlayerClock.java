package ojplg.skir.play;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.state.GameId;
import ojplg.skir.state.event.GameSpecifiable;
import ojplg.skir.state.event.NoMoveReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class PlayerClock implements GameSpecifiable {

    private static final Logger _log = LogManager.getLogger(PlayerClock.class);
    private static final Duration MAX_GAP_ALLOWED = Duration.ofSeconds(300);
    private static final long SLEEP_TIME_IN_SECONDS = MAX_GAP_ALLOWED.getSeconds() + 5;

    private final GameId _gameId;
    private final Fiber _fiber;

    private final Channels _channels;
    private Instant _lastActionTime;

    public PlayerClock(GameId gameId, Channels channels){
        _gameId = gameId;
        _channels = channels;
        _fiber = Skir.createThreadFiber("PlayerClock-" + gameId);
        _channels.subscribeToAdjutant(this,_fiber, this::handleAdjutant);
        _lastActionTime = Instant.now();
    }

    public void start(){
        _fiber.start();
    }

    private void handleAdjutant(Adjutant adjutant){
        _lastActionTime = Instant.now();
        _fiber.schedule(() -> {
            _log.debug("Checking for move. " + _lastActionTime);

            Instant now = Instant.now();
            Instant shouldHaveMovedBy = _lastActionTime.plus(MAX_GAP_ALLOWED);

            if ( now.isAfter(shouldHaveMovedBy)) {
                _log.warn("No move received. Last action " + _lastActionTime + " for game " + _gameId);
                NoMoveReceivedEvent event = new NoMoveReceivedEvent(_gameId);
                _channels.publishNoMoveReceivedEvent(event);
            }
            else {
                _log.debug("Ok.");
            }
        }, SLEEP_TIME_IN_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public GameId getGameId() {
        return _gameId;
    }
}
