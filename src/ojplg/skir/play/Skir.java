package ojplg.skir.play;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.ThreadFiber;
import ojplg.skir.web.UseJetty;

import java.util.Arrays;

public class Skir {

    private static final Logger _log = LogManager.getLogger(Skir.class);

    public static void main(String[] args) {
        _log.info("Starting");

        final Channels channels = new Channels();
        startGameRunner(channels);

        if ( Arrays.asList(args).contains("-bench") ){
            channels.StartGameChannel.publish("Test bench starting");
        } else {
            startWebServer(channels);
        }

        _log.info("Start up complete");
    }

    private static void startGameRunner(Channels channels){
        GameRunner gameRunner = new GameRunner(channels, createThreadFiber("GameRunnerFiber"));
        gameRunner.start();
    }

    private static void startWebServer(Channels channels){
        UseJetty jettyServer = new UseJetty(8080, channels);
        Thread webThread = new Thread(() -> {
            try {
                jettyServer.startJettyServer();
            } catch (Exception ex) {
                _log.error("Could not start web server", ex);
            }
        });
        webThread.setUncaughtExceptionHandler((t, e) -> _log.error("Web thread exception caught at top level", e));
        webThread.start();
    }

    public static ThreadFiber createThreadFiber(String name){
        ThreadFiber fiber = new ThreadFiber(new RunnableExecutorImpl(), name, false);
        fiber.getThread().setUncaughtExceptionHandler((t, e) -> _log.error("Fiber exception caught at top level", e));
        return fiber;
    }
}
