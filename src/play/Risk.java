package play;

import card.StandardCardSet;
import map.Country;
import map.StandardMap;
import map.WorldMap;
import state.Game;
import state.Player;
import web.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Risk {

    private Game _game;

    private int _numberPlayers = 6;
    private static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "Yellow", "Pink "};

    public static void main(String[] args) {
        Risk risk = new Risk();

        System.out.println(risk._game);

        try {

            UseJetty jetty = new UseJetty(8080);
            jetty.StartJettyServer();

            System.out.println("Started the main server");

        } catch (Exception e){
            System.out.println("Could not run server");
            e.printStackTrace();
        }

    }

    public Risk(){
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
        Collections.shuffle(countries);

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
