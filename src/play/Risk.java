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
import play.orders.Adjutant;
import play.orders.OrderType;
import state.Game;
import state.Player;
import web.UseJetty;

import java.io.IOException;
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

    private int _numberPlayers = 6;
    private static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "White", "Pink"};

    private final CountDownLatch _latch = new CountDownLatch(1);

    public static void main(String[] args) {

        _log.info("Starting");

        final Risk risk = new Risk();
        final Channels channels = new Channels();

        boolean randomize = true;

        risk.initializeGame(randomize, channels);
        risk._jettyServer = new UseJetty(8080, risk._game, risk._gameRunner, channels);

        Thread webThread = new Thread(new Runnable(){
            @Override
            public void run(){
                risk.runWebServer();
            }
        },"WebThread");

        webThread.start();

        try {
            risk._latch.await();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        risk.startGame(randomize);

        Thread shellThread = new Thread(new Runnable() {
            @Override
            public void run() {
                risk.runShell();
            }
        },"ShellThread");
        shellThread.start();
    }

    private void runWebServer(){
        try {

            _jettyServer.StartJettyServer(_latch);

        } catch (Exception e){
            _log.error("Could not start jetty", e);
        }
    }


    private void runShell(){
        Shell shell = new Shell(_game);

        try {
            // TODO: this is all screwy and needs to be reversed
            _gameRunner = new GameRunner(_game, shell);
            Adjutant adjutant = _gameRunner.newAdjutant(_roller);
            while(_gameRunner.isGameRunning()) {
                OrderType ot = shell.next(adjutant);
//                _log.info("Next just called with " + _gameRunner.currentPlayer());
                adjutant = shell.handeOrderType(ot, adjutant);
//                _log.info("Adjutant is for " + adjutant.getActivePlayer());
            }

        } catch (QuitException ex){
            _log.info("Quitting");
            _jettyServer.stop();
            return;
        } catch (IOException ex){
            _log.error("IO problem", ex);
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
        for (int idx = 1; idx < _numberPlayers; idx++) {
            Player player = players.get(idx);
            AutomatedPlayer ai = new NeverAttacks(player);
            _game.addAutomatedPlayer(ai);
        }
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
