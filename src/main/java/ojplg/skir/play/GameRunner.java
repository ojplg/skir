package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.card.StandardCardSet;
import ojplg.skir.map.Country;
import ojplg.skir.map.StandardMap;
import ojplg.skir.map.WorldMap;
import ojplg.skir.state.GameId;
import ojplg.skir.state.event.GameSpecifiable;
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

public class GameRunner implements GameSpecifiable {

    private final static Logger _log = LogManager.getLogger(GameRunner.class);
    private final static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "White", "Pink"};

    private final Channels _channels;
    private final Fiber _fiber;
    private final AiFactory _aiFactory;
    private final NewGameRequest _gameRequest;

    private Map<Player,AutomatedPlayer> _automatedPlayers;

    private Adjutant _currentAdjutant;
    private PreGame _preGame;
    private Game _game;

    public GameRunner(AiFactory aiFactory, Channels channels, NewGameRequest gameRequest){
        _channels = channels;
        _gameRequest = gameRequest;
        _aiFactory = aiFactory;
        _preGame = new PreGame(channels);
        _fiber = Skir.createThreadFiber("GameRunner-" + _preGame.getGameId());

        _channels.subscribeToOrder(this, _fiber, this::processOrder);
        _channels.subscribeToClientConnectedEvent(this, _fiber, this::handleClientConnection);
        _channels.StartGameChannel.subscribe(_fiber, this::startGame);
        _channels.subscribeToAdjutant(this, _fiber, this::aiOrderGenerator);
    }

    public void start(){
        _fiber.start();
    }

    public static String colorForIndex(int index){
        return _colors[index];
    }

    private void handleClientConnection(ClientConnectedEvent clientConnectedEvent){
        _log.info("Handling client connection for " + clientConnectedEvent);
        boolean republishAdjutant = _preGame.handleClientConnection(clientConnectedEvent);
        if ( _game != null ) {
            _game.publishAllState();
            if (republishAdjutant) {
                _channels.publishAdjutant(_currentAdjutant);
            }
        }
    }

    private void processOrder(Order order){
        Player player = _currentAdjutant.getActivePlayer();
        _log.debug("Processing order for " + player + " of type " + order.getType());
        _currentAdjutant = order.execute(_game);
        if (_game.gameOver()) {
            handleGameOver();
        } else {
            _channels.publishAdjutant(_currentAdjutant);
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
        if( matches(adjutant)){
            AutomatedPlayer ai = _automatedPlayers.get(adjutant.getActivePlayer());
            if( ai != null ) {
                Order order = ai.generateOrder(_currentAdjutant, _game);
                littleDelay();
                _channels.publishOrder(order);
            }
        }
    }

    private void littleDelay(){
        if( _gameRequest.getDelay() > 0) {
            try {
                Thread.sleep(_gameRequest.getDelay());
            } catch (InterruptedException ie) {
                _log.warn("Who interrupted me?", ie);
            }
        }
    }

    private void startGame(String s){
        _game = initializeGame(_channels);
        assignCountries();
        initializedAIs(_game);
        _game.start();
        _game.publishAllState();
        _currentAdjutant = Adjutant.newGameAdjutant(_game.getGameId(), _game.currentAttacker());
        _channels.publishAdjutant(_currentAdjutant);
        _log.info("Starting game " + s);
    }

    public boolean isStarted(){
        return _game != null;
    }

    public void stop(){
        _fiber.dispose();

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
        _log.info("Initializing game");
        int initialArmies = initialArmyCount(_colors.length);
        Tuple<List<Player>, Map<Player, AutomatedPlayer>> newPlayers = _preGame.newPlayers(_colors, _aiFactory);

        WorldMap map = new StandardMap();
        Roller roller = new RandomRoller(System.currentTimeMillis());
        _automatedPlayers = newPlayers.getSecond();
        GameId gameId = _preGame.getGameId();
        //_preGame.next();

        return new Game(gameId, map, newPlayers.getFirst(), StandardCardSet.deck, roller, channels, initialArmies);
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

    public GameId getGameId(){
        return _preGame.getGameId();
    }

    public NewGameRequest getGameRequest(){
        return _gameRequest;
    }
}
