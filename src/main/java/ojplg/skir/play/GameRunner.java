package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.card.StandardCardSet;
import ojplg.skir.map.Country;
import ojplg.skir.map.StandardMap;
import ojplg.skir.map.WorldMap;
import ojplg.skir.utils.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.state.event.ClientConnectedEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GameRunner {

    private final static Logger _log = LogManager.getLogger(GameRunner.class);
    private final static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "White", "Pink"};

    private final Channels _channels;
    private final Fiber _fiber;
    private final int _orderDelay;
    private final PreGame _preGame;
    private final AiFactory _aiFactory;

    private Map<Player,AutomatedPlayer> _automatedPlayers;

    private Adjutant _currentAdjutant;
    private Game _game;

    public GameRunner(AiFactory aiFactory, Channels channels, Fiber fiber, int orderDelay){
        _channels = channels;
        _fiber = fiber;
        _orderDelay = orderDelay;
        _aiFactory = aiFactory;
        _preGame = new PreGame(channels);

        _channels.OrderEnteredChannel.subscribe(_fiber, this::processOrder);
        _channels.ClientConnectedEventChannel.subscribe(_fiber, this::handleClientConnection);
        _channels.StartGameChannel.subscribe(_fiber, this::startGame);
        _channels.InitializeGameChannel.subscribe(_fiber, this::initializeGame);
        _channels.AdjutantChannel.subscribe(_fiber, this::aiOrderGenerator);
    }

    public void start(){
        _fiber.start();
    }

    public static String colorForIndex(int index){
        return _colors[index];
    }

    private void handleClientConnection(ClientConnectedEvent clientConnectedEvent){
        boolean rePublishState = _preGame.handleClientConnection(clientConnectedEvent);
        if( rePublishState){
            _game.publishAllState();
            _channels.AdjutantChannel.publish(_currentAdjutant);
        }
    }

    private void processOrder(Order order){
        Player player = _currentAdjutant.getActivePlayer();
        _log.debug("Processing order for " + player + " of type " + order.getType());
        _currentAdjutant = order.execute(_game);
        if( _game.gameOver() ){
            handleGameOver();
        } else {
            _channels.AdjutantChannel.publish(_currentAdjutant);
        }
    }

    private void handleGameOver(){
        List<Player> remainingPlayers = _game.getAllPlayers();
        boolean draw = remainingPlayers.size() > 1;
        String result = draw ? "Draw" : "Victory";
        _log.info("Game over on turn " + _game.getTurnNumber() + ". Result: " + result);
        remainingPlayers.forEach( p-> {
            AutomatedPlayer ai = _automatedPlayers.get(p);
            String aiMessage = "";
            if( ai != null){
                aiMessage = " AI of type " + ai.getClass();
            }
            if( draw ) {
                _log.info("Survivor: " + p + aiMessage);
            } else {
                _log.info("Victor: " + p + aiMessage);
            }
        });
    }

    private void aiOrderGenerator(Adjutant adjutant){
        AutomatedPlayer ai = _automatedPlayers.get(adjutant.getActivePlayer());
        if( ai != null ){
            Order order = ai.generateOrder(_currentAdjutant, _game);
            littleDelay();
            _channels.OrderEnteredChannel.publish(order);
        }
    }

    private void littleDelay(){
        if( _orderDelay > 0) {
            try {
                Thread.sleep(_orderDelay);
            } catch (InterruptedException ie) {
                _log.warn("Who interrupted me?", ie);
            }
        }
    }

    private void initializeGame(String s){
        _log.info("Intializing game " + s);
        _game = initializeGame(_channels);
    }

    private void startGame(String s){
        _log.info("Starting game " + s);
        initializeGame("Yes " + s);
        assignCountries();
        initializedAIs(_game);
        _game.start();
        _game.publishAllState();
        _currentAdjutant = Adjutant.newGameAdjutant(_game.currentAttacker());
        _channels.AdjutantChannel.publish(_currentAdjutant);
    }

    private void initializedAIs(Game game){
        for(Player player: game.getAllPlayers()){
            AutomatedPlayer ai = _automatedPlayers.get(player);
            if( ai != null ){
                ai.initialize(game);
            }
        }
    }

    private Game initializeGame(Channels channels) {
        int initialArmies = initialArmyCount(_colors.length);
        Tuple<List<Player>, Map<Player, AutomatedPlayer>> newPlayers = _preGame.newPlayers(_colors, _aiFactory);

        WorldMap map = new StandardMap();
        Roller roller = new RandomRoller(System.currentTimeMillis());
        _automatedPlayers = newPlayers.getSecond();

        return new Game(map, newPlayers.getFirst(), StandardCardSet.deck, roller, channels, initialArmies);
    }

    private void assignCountries(){
        List<Country> countries = _game.getAllCountries();
        Collections.shuffle(countries);

        List<Player> players = _game.getAllPlayers();
        for(int idx=0; idx<countries.size(); idx++){
            Player player = players.get(idx%_colors.length);
            Country country = countries.get(idx);
            _game.processPlaceArmyOrder(player, country, 1);
        }
        _game.doInitialPlacements();
    }

    private int initialArmyCount(int numberPlayers){
        return 20 + (5 * (6 - numberPlayers));
    }

}
