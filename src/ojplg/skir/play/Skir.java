package ojplg.skir.play;

import ojplg.skir.play.bench.AiTestBench;
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
        final boolean benchTest = Arrays.asList(args).contains("-bench");

        startGameRunner(channels, benchTest);

        if ( benchTest ){
            AiTestBench testBench = new AiTestBench(channels, createThreadFiber("AiTestBenchFiber"),1000);
            testBench.start();
        } else {
            startWebServer(channels);
        }

        _log.info("Start up complete");
    }

    private static void startGameRunner(Channels channels, boolean benchTest){
        int turnDelay = benchTest ? 0 : 30;
        GameRunner gameRunner = new GameRunner(channels, createThreadFiber("GameRunnerFiber"), turnDelay);
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
