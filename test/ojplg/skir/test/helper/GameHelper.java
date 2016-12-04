package ojplg.skir.test.helper;

import ojplg.skir.card.StandardCardSet;
import ojplg.skir.map.Country;
import ojplg.skir.map.StandardMap;
import ojplg.skir.play.Channels;
import ojplg.skir.play.RandomRoller;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

import java.util.ArrayList;
import java.util.List;

public class GameHelper {

    public Game Game;

    public GameHelper(){
        Game = testingGameState();
    }

    private Game testingGameState(){
        List<Player> players = new ArrayList<>();
        Player red = new Player("red");
        red.grantReserves(8);
        Player white = new Player("white");
        Player blue = new Player("blue");
        players.add(red);
        players.add(white);
        players.add(blue);
        Channels channels = new Channels();

        Game game = new Game(
                new StandardMap(),
                players,
                StandardCardSet.deck,
                new RandomRoller(0),
                channels);

        game.placeArmies(red, Country.Afghanistan, 8);
        game.placeArmies(white, Country.Middle_East, 0);
        game.start();

        return game;
    }

}
