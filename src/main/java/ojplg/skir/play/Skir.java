package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.evolve.EvolutionRunner;
import ojplg.skir.play.bench.AiTestBench;
import ojplg.skir.state.Constants;
import ojplg.skir.web.WebRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.ThreadFiber;
import ojplg.skir.web.JettyInitializer;

import java.util.Arrays;

public class Skir {

    private static final Logger _log = LogManager.getLogger(Skir.class);

    public static void main(String[] args) {
        _log.info("Starting");

        final Channels channels = new Channels();
        final boolean benchTest = Arrays.asList(args).contains("-bench");
        final boolean evolve = Arrays.asList(args).contains("-evolve");
        final AiFactory aiFactory = new AiFactory(Constants.AI_NAMES);

        if ( benchTest ){
            AiTestBench testBench = new AiTestBench(aiFactory, channels, createThreadFiber("AiTestBenchFiber"),
                    Constants.NUMBER_BENCH_GAMES_TO_RUN);
            testBench.start();
            testBench.startRun();
        } else if ( evolve ) {
            EvolutionRunner evolutionRunner = new EvolutionRunner(channels, createThreadFiber("EvolutionFiber"));
            GameRunner gameRunner = new GameRunner(aiFactory, channels, NewGameRequest.aiEvolution());
            gameRunner.start();
            evolutionRunner.evolve(aiFactory);
        } else {
            startWebServer(channels);
        }

        _log.info("Start up complete");
    }

    private static void startWebServer(Channels channels){
        String environmentPort = System.getenv("PORT");
        _log.info("Environment port is " + environmentPort);
        int port = environmentPort != null ? Integer.parseInt(environmentPort) : 8080;
        _log.info("Using port " + port);
        WebRunner webRunner = new WebRunner(channels);
        JettyInitializer jettyServer = new JettyInitializer(port, channels, webRunner);
        Thread webThread = new Thread(() -> {
            try {
                jettyServer.startJettyServer();
            } catch (Exception ex) {
                _log.error("Could not start web server", ex);
            }
        });
        webThread.setUncaughtExceptionHandler((t, e) -> _log.error("Web thread exception caught at top level", e));
        webThread.start();
        webRunner.start();
    }

    public static ThreadFiber createThreadFiber(String name){
        ThreadFiber fiber = new ThreadFiber(new RunnableExecutorImpl(), name, false);
        fiber.getThread().setUncaughtExceptionHandler((t, e) -> _log.error("Fiber exception caught at top level", e));
        return fiber;
    }
}
