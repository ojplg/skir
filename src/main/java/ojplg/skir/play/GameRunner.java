package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.card.StandardCardSet;
import ojplg.skir.map.Country;
import ojplg.skir.map.StandardMap;
import ojplg.skir.map.WorldMap;
import ojplg.skir.state.event.GameEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.state.event.ClientConnectedEvent;
import ojplg.skir.state.event.GameJoinedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameRunner {

    private final static Logger _log = LogManager.getLogger(GameRunner.class);
    private final static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "White", "Pink"};

    private final Map<ClientConnectedEvent, Player> _remotePlayerInfo = new HashMap<>();
    private final Set<Player> _remotePlayers = new HashSet<>();
    private final Channels _channels;
    private final Fiber _fiber;
    private final int _orderDelay;
    private final AiFactory _aiFactory;

    private Adjutant _currentAdjutant;
    private boolean _gameStarted = false;
    private Game _game;

    public GameRunner(AiFactory aiFactory, Channels channels, Fiber fiber, int orderDelay){
        _channels = channels;
        _fiber = fiber;
        _orderDelay = orderDelay;
        _aiFactory = aiFactory;

        _channels.OrderEnteredChannel.subscribe(_fiber, this::processOrder);
        _channels.ClientConnectedEventChannel.subscribe(_fiber, this::handleClientConnection);
        _channels.StartGameChannel.subscribe(_fiber, this::startGame);
        _channels.InitializeGameChannel.subscribe(_fiber, this::initializeGame);
        _channels.AdjutantChannel.subscribe(_fiber, this::aiOrderGenerator);
    }

    public void start(){
        _fiber.start();
    }

    private void addAutomatedPlayers(){
        for (int idx = 0; idx < _colors.length; idx++) {
            Player player = _game.getAllPlayers().get(idx);
            if(! _remotePlayers.contains(player)) {
                AutomatedPlayer ai = _aiFactory.generateAiPlayer(player);
                _log.info("Adding ai for " + player + ", " + ai.getClass());
                _channels.GameEventChannel.publish(GameEvent.joinsGame(player));
                player.setAutomatedPlayer(ai);
                ai.initialize(_game);
            }
        }
    }

    private void handleClientConnection(ClientConnectedEvent clientConnectedEvent){

        _log.info("Client connected " + clientConnectedEvent);

        int availablePlayerNumber = _remotePlayerInfo.size();
        if( availablePlayerNumber == 0 ){
            initializeGame("Crap");
        }

        if (_remotePlayerInfo.containsKey(clientConnectedEvent)){
            Player player = _remotePlayerInfo.get(clientConnectedEvent);
            _log.info("Player rejoined " + clientConnectedEvent + ", " + player);
            _game.publishAllState();
            GameJoinedEvent gameJoinedEvent = new GameJoinedEvent(
                    clientConnectedEvent, player, false);
            _channels.GameJoinedEventChannel.publish(gameJoinedEvent);
            _channels.AdjutantChannel.publish(_currentAdjutant);
        } else if ( "demo".equalsIgnoreCase(clientConnectedEvent.getDisplayName()) ) {
            _log.info("Demo");
        } else if ( playerSlotAvailable() ) {
            _log.info("Trying to add a new player " + clientConnectedEvent);
            Player player = _game.getAllPlayers().get(availablePlayerNumber);

            _remotePlayerInfo.put(clientConnectedEvent, player);
            _remotePlayers.add(player);
            player.setClientKey(clientConnectedEvent.getClientKey());
            player.setDisplayName(clientConnectedEvent.getDisplayName());

            _log.info("Player " + availablePlayerNumber + " who is " + player.getColor());

            GameJoinedEvent gameJoinedEvent = new GameJoinedEvent(
                    clientConnectedEvent, player, availablePlayerNumber == 0);
            _channels.GameJoinedEventChannel.publish(gameJoinedEvent);
            _channels.GameEventChannel.publish(GameEvent.joinsGame(player));
            _log.info("Published game joined event " + gameJoinedEvent);
        } else {
            _log.info("Could not join the game " + clientConnectedEvent);
        }
    }

    private boolean playerSlotAvailable(){
        return ! _gameStarted;
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
            AutomatedPlayer ai = p.getAutomatedPlayer();
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
        AutomatedPlayer ai = adjutant.getActivePlayer().getAutomatedPlayer();
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
        assignCountries();
        addAutomatedPlayers();
        _game.start();
        _gameStarted = true;
        _game.publishAllState();
        _currentAdjutant = Adjutant.newGameAdjutant(_game.currentAttacker());
        _channels.AdjutantChannel.publish(_currentAdjutant);
    }

    private Game initializeGame(Channels channels) {
        int initialArmies = initialArmyCount(_colors.length);
        PreGame preGame = new PreGame();
        List<Player> players = preGame.newPlayers(_colors, initialArmies);

        WorldMap map = new StandardMap();
        Roller roller = new RandomRoller(System.currentTimeMillis());

        return new Game(map, players, StandardCardSet.deck, roller, channels);
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
