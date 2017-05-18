package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.evolve.EvolutionRunner;
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
        final boolean evolve = Arrays.asList(args).contains("-evolve");


        if ( benchTest ){
            AiFactory aiFactory = new AiFactory();
            AiTestBench testBench = new AiTestBench(aiFactory, channels, createThreadFiber("AiTestBenchFiber"),10);
            testBench.start();
        } else if ( evolve ) {
            EvolutionRunner evolutionRunner = new EvolutionRunner(channels, createThreadFiber("EvolutionFiber"));
            evolutionRunner.evolve();
        } else {
            AiFactory aiFactory = new AiFactory();
            startGameRunner(aiFactory, channels, true);
            startWebServer(channels);
        }

        _log.info("Start up complete");
    }

    private static void startGameRunner(AiFactory aiFactory, Channels channels, boolean useDelay){
        int turnDelay = useDelay ? 0 : 40;
        GameRunner gameRunner = new GameRunner(aiFactory, channels,
                createThreadFiber("GameRunnerFiber"), turnDelay);
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
