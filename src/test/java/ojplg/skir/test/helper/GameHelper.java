package ojplg.skir.test.helper;

import ojplg.skir.card.StandardCardSet;
import ojplg.skir.map.Country;
import ojplg.skir.map.StandardMap;
import ojplg.skir.map.WorldMap;
import ojplg.skir.play.Channels;
import ojplg.skir.play.RandomRoller;
import ojplg.skir.state.Game;
import ojplg.skir.state.GameId;
import ojplg.skir.state.Occupations;
import ojplg.skir.state.Player;
import ojplg.skir.state.PlayerHoldings;

import java.util.ArrayList;
import java.util.List;

public class GameHelper {
    public Game Game;
    private Occupations _occupations;
    private GameId _gameId;

    public Player RedPlayer;
    public Player WhitePlayer;
    public Player GreenPlayer;
    public Player BluePlayer;
    public Player BlackPlayer;

    public GameHelper(){
        _gameId = GameId.next();
        RedPlayer = new Player(_gameId,"Red",0);
        WhitePlayer = new Player(_gameId,"White",1);
        GreenPlayer = new Player(_gameId,"Green",2);
        BluePlayer = new Player(_gameId,"Blue",3);
        BlackPlayer = new Player(_gameId,"Black",4);
        Game = baseGameState();
        }

    public void setCountryTroopLevel(Country country, int level){
        int currentLevel = _occupations.getOccupationForce(country);
        Player currentOwner = _occupations.getOccupier(country);
        _occupations.killArmies(country, currentLevel);
        _occupations.placeArmies( currentOwner, country, level);
    }

    public void setCountry(Country country, Player owner, int level){
        int currentLevel = _occupations.getOccupationForce(country);
        _occupations.killArmies(country, currentLevel);
        _occupations.placeArmies(owner, country, level);
    }

    public void setAllCountries(Player owner, int level){
        for(Country country : _occupations.getMap().getAllCountries()){
            setCountry(country, owner, level);
        }
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
                _gameId,
                players,
                StandardCardSet.deck,
                new RandomRoller(0),
                channels,
                _occupations,
                0);

        int cnt = 0;

        game.start();

        for(Country country : game.getAllCountries()){
            Player player = players.get(cnt % players.size());
            PlayerHoldings holdings = game.getPlayerHoldings(player);
            holdings.grantReserves(1);
            game.processPlaceArmyOrder(player, country, 1, false);
            cnt++;
        }


        return game;
    }

    public void setUpPlayerForAttack(Player attacker){
        for(Country country: Game.findOccupiedCountries(attacker)){
            Game.getPlayerHoldings(attacker).grantReserves(4);
            Game.processPlaceArmyOrder(attacker, country, 4);
        }
    }

    public void setUpPreEliminationCondition(Player victor, Player deathbedPlayer, Country attackingCountry, Country conqueredCountry){
        for(Country country : Game.findOccupiedCountries(deathbedPlayer)){
            _occupations.killArmies(country, 1);
            _occupations.placeArmies(victor,country, 1);
        }
        Game.getPlayerHoldings(victor).grantReserves(5);
        Game.processPlaceArmyOrder(victor, attackingCountry, 5);
        Game.getPlayerHoldings(deathbedPlayer).grantReserves(1);
        _occupations.killArmies(conqueredCountry, 1);
        Game.processPlaceArmyOrder(deathbedPlayer, conqueredCountry, 1, false);
        setCountryTroopLevel(conqueredCountry, 0);
    }
}
