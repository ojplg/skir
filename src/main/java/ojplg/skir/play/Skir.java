package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.evolve.EvolutionRunner;
import ojplg.skir.play.bench.AiTestBench;
import ojplg.skir.state.Constants;
import ojplg.skir.web.WebRunner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.ThreadFiber;
import ojplg.skir.web.JettyInitializer;

import java.util.Arrays;
import java.util.List;

public class Skir {

    private static final Logger _log = LogManager.getLogger(Skir.class);

    public static void main(String[] args) {
        _log.info("Starting");

        CommandLine commandLine = parseOptions(args);

        final List<String> aiNames;
        if( commandLine.hasOption("ais")) {
            aiNames = Arrays.asList(commandLine.getOptionValues("ais"));
        } else {
            aiNames = AiFactory.allPlayerNames();
        }

        final AiFactory aiFactory = new AiFactory(aiNames);
        final Channels channels = new Channels();

        if( commandLine.hasOption("help")) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("skir", cliOptions());
        } else if ( commandLine.hasOption("bench")){
            AiTestBench testBench = new AiTestBench(aiFactory, channels, createThreadFiber("AiTestBenchFiber"),
                    Constants.NUMBER_BENCH_GAMES_TO_RUN);
            testBench.start();
            testBench.startRun();
        } else if ( commandLine.hasOption("evolve") ) {
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

    private static CommandLine parseOptions(String[] args){
        try {
            Options options = cliOptions();
            DefaultParser optionsParser = new DefaultParser();
            return optionsParser.parse(options, args);
        } catch (ParseException pe){
            throw new RuntimeException(pe);
        }
    }

    private static Options cliOptions(){
        Options options = new Options();
        options.addOption(new Option("b","bench", false, "Run in test bench mode."));
        options.addOption(new Option("e", "evolve", false, "Run in evolve mode."));
        options.addOption(new Option("h", "help", false, "See options."));
        Option aisOption = new Option("a", "ais", true, "Specify AIs eligible for use during run");
        aisOption.setValueSeparator(',');
        aisOption.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(aisOption);
        return options;
    }
}
