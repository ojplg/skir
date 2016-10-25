package play;

import card.StandardCardSet;
import cli.Shell;
import map.Country;
import map.StandardMap;
import map.WorldMap;
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

    private Game _game;
    private Roller _roller = new RandomRoller(1);
    private UseJetty _jettyServer;

    private int _numberPlayers = 6;
    private static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "Yellow", "Pink "};

    private final CountDownLatch _latch = new CountDownLatch(1);

    public static void main(String[] args) {

        final Risk risk = new Risk();
        risk.initializeGame(false);
        risk._jettyServer = new UseJetty(8080, risk._game);

        Thread webThread = new Thread(new Runnable(){
            @Override
            public void run(){
                risk.runWebServer();
            }
        });
        webThread.start();

        try {
            risk._latch.await();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        risk.startGame(false);

        Thread shellThread = new Thread(new Runnable() {
            @Override
            public void run() {
                risk.runShell();
            }
        });
        shellThread.start();
    }

    private void runWebServer(){
        try {

            _jettyServer.StartJettyServer(_latch);

        } catch (Exception e){
            System.out.println("Could not run server");
            e.printStackTrace();
        }
    }


    private void runShell(){
        Shell shell = new Shell(_game);

        try {
            Adjutant adjutant = new Adjutant(_game.currentAttacker(), _roller);
            while(true) {
                OrderType ot = shell.next(adjutant);
                adjutant = shell.handeOrderType(ot, adjutant);
            }

        } catch (QuitException ex){
            System.out.println("Quitting");
            _jettyServer.stop();
            return;
        } catch (IOException ex){
            System.out.println("IO problem");
            ex.printStackTrace();
        }

    }

    private void initializeGame(boolean randomize) {
        List<Player> players = new ArrayList<Player>();
        int initialArmies = initialArmyCount(_numberPlayers);
        for (int idx = 0; idx < _numberPlayers; idx++) {
            Player player = new Player(_colors[idx]);
            player.grantReserves(initialArmies);
            players.add(player);
        }

        WorldMap map = new StandardMap();
        _game = new Game(map, players, StandardCardSet.deck);
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
            System.out.println("Placing player " + player.getColor() + " into " + country.getName());
            _game.placeArmy(player, country);
        }
        _game.doInitialPlacements();
    }

    private int initialArmyCount(int numberPlayers){
        return 20 + (5 * (6 - numberPlayers));
    }
}
