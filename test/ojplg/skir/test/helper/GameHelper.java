package ojplg.skir.test.helper;

import ojplg.skir.card.StandardCardSet;
import ojplg.skir.map.Country;
import ojplg.skir.map.StandardMap;
import ojplg.skir.map.WorldMap;
import ojplg.skir.play.Channels;
import ojplg.skir.play.RandomRoller;
import ojplg.skir.state.Game;
import ojplg.skir.state.Occupations;
import ojplg.skir.state.Player;

import java.util.ArrayList;
import java.util.List;

public class GameHelper {

    public Game Game;
    private Occupations _occupations;

    public Player RedPlayer = new Player("Red");
    public Player WhitePlayer = new Player("White");
    public Player GreenPlayer = new Player("Green");
    public Player BluePlayer = new Player("Blue");
    public Player BlackPlayer = new Player("Black");

    public GameHelper(){
        Game = baseGameState();
    }

    public void setCountryTroopLevel(Country country, int level){
        int currentLevel = _occupations.getOccupationForce(country);
        Player currentOwner = _occupations.getOccupier(country);
        _occupations.killArmies(country, currentLevel);
        _occupations.placeArmies( currentOwner, country, level);
    }

    private Game baseGameState(){

        List<Player> players = new ArrayList<>();
        players.add(RedPlayer);
        players.add(WhitePlayer);
        players.add(GreenPlayer);
        players.add(BluePlayer);
        players.add(BlackPlayer);

        Channels channels = new Channels();

        WorldMap worldMap = new StandardMap();
        _occupations = new Occupations(worldMap);

        Game game = new Game(
                players,
                StandardCardSet.deck,
                new RandomRoller(0),
                channels,
                _occupations);

        int cnt = 0;
        for(Country country : game.getAllCountries()){
            Player player = players.get(cnt % players.size());
            player.grantReserves(1);
            game.processPlaceArmyOrder(player, country, 1);
            cnt++;
        }

        game.start();

        return game;
    }

    public void setUpPlayerForAttack(Player attacker){
        for(Country country: Game.findOccupiedCountries(attacker)){
            attacker.grantReserves(4);
            Game.processPlaceArmyOrder(attacker, country, 4);
        }
    }

    public void setUpPreEliminationCondition(Player victor, Player deathbedPlayer, Country attackingCountry, Country conqueredCountry){
        for(Country country : Game.findOccupiedCountries(deathbedPlayer)){
            _occupations.killArmies(country, 1);
            _occupations.placeArmies(victor,country, 1);
        }
        victor.grantReserves(5);
        Game.processPlaceArmyOrder(victor, attackingCountry, 5);
        deathbedPlayer.grantReserves(1);
        _occupations.killArmies(conqueredCountry, 1);
        Game.processPlaceArmyOrder(deathbedPlayer, conqueredCountry, 1);
        setCountryTroopLevel(conqueredCountry, 0);
    }
}
