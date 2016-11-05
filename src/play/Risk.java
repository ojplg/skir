package play;

import ai.AutomatedPlayer;
import ai.NeverAttacks;
import card.StandardCardSet;
import cli.Shell;
import map.Country;
import map.StandardMap;
import map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.ThreadFiber;
import state.Game;
import state.Player;
import web.UseJetty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Risk {

    private static final Logger _log = LogManager.getLogger(Risk.class);

    private Game _game;
    private GameRunner _gameRunner;
    private final Roller _roller = new RandomRoller(1);
    private UseJetty _jettyServer;
    private Shell _shell;

    private int _numberPlayers = 6;
    private static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "White", "Pink"};

    private final CountDownLatch _latch = new CountDownLatch(1);

    public static void main(String[] args) {
        _log.info("Starting");

        final Risk risk = new Risk();
        final Channels channels = new Channels();

        boolean randomize = true;

        risk.initializeGame(randomize, channels);

        ThreadFiber webFiber = new ThreadFiber(new RunnableExecutorImpl(), "WebFiber", true);
        risk._jettyServer = new UseJetty(8080, channels, webFiber);

        Thread webThread = new Thread(new Runnable(){
            @Override
            public void run(){
                risk.runWebServer();
            }
        },"WebThread");

        webThread.start();
        webFiber.start();

        try {
            risk._latch.await();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        risk.startGame(randomize);

        ThreadFiber gameRunnerFiber = new ThreadFiber(new RunnableExecutorImpl(), "GameRunnerFiber", true);
        risk._gameRunner = new GameRunner(risk._game, channels, gameRunnerFiber);
        for (int idx = 1; idx < risk._numberPlayers; idx++) {
            Player player = risk._game.getAllPlayers().get(idx);
            AutomatedPlayer ai = new NeverAttacks(player);
            risk._gameRunner.addAutomatedPlayer(ai);
        }

        gameRunnerFiber.start();

        ThreadFiber shellFiber = new ThreadFiber(new RunnableExecutorImpl(), "ShellFiber", true);
        risk._shell = new Shell(risk._game, channels, shellFiber);
        shellFiber.start();

        risk._gameRunner.startGame(risk._roller);
    }

    private void runWebServer(){
        try {

            _jettyServer.StartJettyServer(_latch);

        } catch (Exception e){
            _log.error("Could not start jetty", e);
        }
    }

    private void initializeGame(boolean randomize, Channels channels) {
        List<Player> players = new ArrayList<Player>();
        int initialArmies = initialArmyCount(_numberPlayers);
        for (int idx = 0; idx < _numberPlayers; idx++) {
            Player player = new Player(_colors[idx]);
            player.grantReserves(initialArmies);
            players.add(player);
        }

        WorldMap map = new StandardMap();
        Roller roller;
        if( randomize ) {
            roller = new RandomRoller(System.currentTimeMillis());
        } else {
            roller = new RandomRoller(1);
        }

        _game = new Game(map, players, StandardCardSet.deck, roller, channels);
    }

    private void startGame(boolean randomize){
        List<Country> countries = _game.getAllCountries();
        if( randomize) {
            Collections.shuffle(countries);
        }

        List<Player> players = _game.getAllPlayers();
        for(int idx=0; idx<countries.size(); idx++){
            Player player = players.get(idx%_numberPlayers);
            Country country = countries.get(idx);
            _game.placeArmy(player, country);
        }
        _game.doInitialPlacements();
    }

    private int initialArmyCount(int numberPlayers){
        return 20 + (5 * (6 - numberPlayers));
    }
}
