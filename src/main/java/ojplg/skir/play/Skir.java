package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.evolve.EvolutionRunner;
import ojplg.skir.evolve.EvolutionSettings;
import ojplg.skir.play.bench.AiTestBench;
import ojplg.skir.state.Constants;
import ojplg.skir.state.GameId;
import ojplg.skir.state.GameState;
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
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;
import ojplg.skir.web.JettyInitializer;

import java.util.Arrays;
import java.util.List;

public class Skir {

    private static final Logger _log = LogManager.getLogger(Skir.class);

    public static void main(String[] args) {
        _log.info("Starting");

        CommandLine commandLine = parseOptions(args);

        if( commandLine.hasOption("help")) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("skir", cliOptions());
        } else if ( commandLine.hasOption("bench")){
            startInTestBenchMode(commandLine);
        } else if (commandLine.hasOption("evolve") ) {
            startInEvolveMode(commandLine);
        } else {
            startWebServer();
        }
    }

    private static void startInEvolveMode(CommandLine commandLine){
        int gamesPerIndividual = intValue(commandLine, "rounds", EvolutionSettings.GAMES_PER_INDIVIDUAL);
        int numberOfGenerations = intValue(commandLine, "generations", EvolutionSettings.NUMBER_OF_GENERATIONS);
        int generationSize = intValue(commandLine, "size", EvolutionSettings.GENERATION_SIZE);
        EvolutionSettings evSettings = new EvolutionSettings(gamesPerIndividual, numberOfGenerations, generationSize);

        Channels channels = new Channels();
        AiFactory aiFactory = new AiFactory(availableAiNames(commandLine));

        EvolutionRunner evolutionRunner = new EvolutionRunner(aiFactory, channels, createMasterFiber("EvolutionFiber"), evSettings);
        evolutionRunner.start();
    }

    private static void startInTestBenchMode(CommandLine commandLine){
        int numberOfRounds = intValue(commandLine, "rounds", Constants.NUMBER_BENCH_GAMES_TO_RUN);
        Channels channels = new Channels();
        AiFactory aiFactory = new AiFactory(availableAiNames(commandLine));
        AiTestBench testBench = new AiTestBench(aiFactory, channels, createMasterFiber("AiTestBenchFiber"),
                numberOfRounds, true);
        testBench.start();
        testBench.startRun();
    }

    private static List<String> availableAiNames(CommandLine commandLine){
        if( commandLine.hasOption("ais")) {
            return Arrays.asList(commandLine.getOptionValues("ais"));
        } else {
            return AiFactory.allPlayerNames();
        }
    }

    private static int intValue(CommandLine commandLine, String optionName, int defaultValue){
        if( commandLine.hasOption(optionName)){
            String stringValue = commandLine.getOptionValue(optionName);
            return Integer.parseInt(stringValue);
        }
        return defaultValue;
    }

    private static void startWebServer(){
        String environmentPort = System.getenv("PORT");
        _log.info("Environment port is " + environmentPort);
        int port = environmentPort != null ? Integer.parseInt(environmentPort) : Constants.DEFAULT_PORT;
        _log.info("Using port " + port);
        Channels channels = new Channels();
        WebRunner webRunner = new WebRunner(channels, createMasterFiber("WebRunner"));
        JettyInitializer jettyServer = new JettyInitializer(port, channels, webRunner);
        Thread webThread = new Thread(() -> {
            try {
                jettyServer.startJettyServer();
            } catch (Exception ex) {
                _log.error("Could not start web server", ex);
            }
        });
        List<GameId> games = GameSaver.availableGames();
        _log.info("Loaded games " + games);
        webThread.setUncaughtExceptionHandler((t, e) -> _log.error("Web thread exception caught at top level", e));
        webThread.start();
        webRunner.start();
        games.forEach(
                gameId -> {
                    GameState state = GameSaver.loadGameState(gameId);
                    if( state != null && ! state.isOver() ){
                        channels.publishRestoreGame(state);
                    }
                }
        );
    }

    private static Fiber createMasterFiber(String name){
        ThreadFiber fiber = new ThreadFiber(new RunnableExecutorImpl(), name, false);
        fiber.getThread().setUncaughtExceptionHandler((t, e) -> _log.error("Fiber exception caught at top level", e));
        return fiber;
    }

    public static Fiber createThreadFiber(String name){
        ThreadFiber fiber = new ThreadFiber(new RunnableExecutorImpl(), name, true);
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
        options.addOption(new Option("r", "rounds", true, "Number of rounds to run for test bench or per individual during evolution"));
        options.addOption(new Option("g", "generations", true, "Number of generations to run during evolution"));
        options.addOption(new Option("s", "size", true, "Number of individuals per generation run during evolution"));
        Option aisOption = new Option("a", "ais", true, "Specify AIs eligible for use during run");
        aisOption.setValueSeparator(',');
        aisOption.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(aisOption);
        return options;
    }
}
