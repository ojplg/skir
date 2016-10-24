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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Risk {

    private Game _game;

    private int _numberPlayers = 6;
    private static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "Yellow", "Pink "};

    public static void main(String[] args) {
        Risk risk = new Risk(false);
        Roller roller = new RandomRoller(1);

        System.out.println(risk._game);
        Shell shell = new Shell(risk._game);

        try {
            Adjutant adjutant = new Adjutant(risk._game.currentAttacker(), roller);
            while(true) {
                OrderType ot = shell.next(adjutant);
                adjutant = shell.handeOrderType(ot, adjutant, risk._game);
            }

        } catch (QuitException ex){
            System.out.println("Quitting");
            return;
        } catch (IOException ex){
            System.out.println("IO problem");
            ex.printStackTrace();
        }

//        try {
//
//            //UseJetty jetty = new UseJetty(8080);
//            //jetty.StartJettyServer();
//
//            //System.out.println("Started the main server");
//
//        } catch (Exception e){
//            System.out.println("Could not run server");
//            e.printStackTrace();
//        }

    }

    public Risk(boolean randomize){
        List<Player> players = new ArrayList<Player>();
        int initialArmies = initialArmyCount(_numberPlayers);
        for (int idx=0 ; idx< _numberPlayers ; idx++ ){
            Player player = new Player(_colors[idx]);
            player.grantReserves(initialArmies);
            players.add(player);
        }

        WorldMap map = new StandardMap();
        _game = new Game(map, players, StandardCardSet.deck);

        List<Country> countries = map.getAllCountries();
        if( randomize) {
            Collections.shuffle(countries);
        }

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
