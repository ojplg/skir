package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.card.StandardCardSet;
import ojplg.skir.map.Country;
import ojplg.skir.map.StandardMap;
import ojplg.skir.map.WorldMap;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.play.orders.Transitions;
import ojplg.skir.state.GameException;
import ojplg.skir.state.GameId;
import ojplg.skir.state.GameState;
import ojplg.skir.state.PlayerHoldings;
import ojplg.skir.state.event.GameSpecifiable;
import ojplg.skir.state.event.GameStartRequest;
import ojplg.skir.state.event.NoMoveReceivedEvent;
import ojplg.skir.utils.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.state.event.ClientConnectedEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameRunner implements GameSpecifiable {

    private final static Logger _log = LogManager.getLogger(GameRunner.class);
    private final static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "White", "Pink"};

    private final Channels _channels;
    private final Fiber _fiber;
    private final AiFactory _aiFactory;
    private final NewGameRequest _gameRequest;
    private final PlayerClock _playerClock;

    private Map<Player,AutomatedPlayer> _automatedPlayers;

    private OrderType _lastOrderType = OrderType.EndTurn;
    private Adjutant _currentAdjutant;
    private PreGame _preGame;
    private Game _game;

    public GameRunner(AiFactory aiFactory, Channels channels, NewGameRequest gameRequest){
        _channels = channels;
        _gameRequest = gameRequest;
        _aiFactory = aiFactory;
        _preGame = new PreGame(channels, gameRequest.getGameId());
        _fiber = Skir.createThreadFiber("GameRunner-" + _preGame.getGameId());
        _playerClock = new PlayerClock(_preGame.getGameId(), _channels);

        _channels.subscribeToOrder(this, _fiber, this::processOrder);
        _channels.subscribeToClientConnectedEvent(this, _fiber, this::handleClientConnection);
        _channels.subscribeToGameStartRequest(this, _fiber, this::startGame);
        _channels.subscribeToAdjutant(this, _fiber, this::aiOrderGenerator);
        _channels.subscribeToNoMoveReceviedEvent(this, _fiber, this::handleNoMoveReceivedEvent);
    }

    public GameRunner(Channels channels, GameState gameState ){
        _channels = channels;
        _gameRequest = NewGameRequest.restoreGame(gameState.getGameId());
        _aiFactory = null;
        _fiber = Skir.createThreadFiber("GameRunner-" + gameState.getGameId());
        _playerClock = new PlayerClock(gameState.getGameId(), channels);

        _channels.subscribeToOrder(this, _fiber, this::processOrder);
        _channels.subscribeToClientConnectedEvent(this, _fiber, this::handleClientConnection);
        _channels.subscribeToGameStartRequest(this, _fiber, this::startGame);
        _channels.subscribeToAdjutant(this, _fiber, this::aiOrderGenerator);
        _channels.subscribeToNoMoveReceviedEvent(this, _fiber, this::handleNoMoveReceivedEvent);

        Roller roller = new RandomRoller(System.currentTimeMillis());

        _preGame = new PreGame(channels, gameState.getGameId());
        _game = new Game(gameState, roller, channels);

        _automatedPlayers = new HashMap<>();
        for(Map.Entry<Player, String> aiPlayerEntry : gameState.getAiPlayerNames().entrySet()){
            AutomatedPlayer automatedPlayer = AiFactory.generateAiInstance(aiPlayerEntry.getValue(), aiPlayerEntry.getKey());
            automatedPlayer.initialize(_game);
            _automatedPlayers.put(aiPlayerEntry.getKey(), automatedPlayer);
        }
    }

    public void start(){
        _fiber.start();
        _playerClock.start();
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
        if( ! _currentAdjutant.isAllowedOrderType(order)) {
            // TODO: Improve logging.
            _log.warn("Disallowed order: " + order);
            return;
        }
        if ( !Transitions.isPossibleTransition(_lastOrderType, order.getType())){
            throw new IllegalArgumentException("Cannot do order " + order.getType() + " following " + _lastOrderType);
        }
        _lastOrderType = order.getType();

        Player player = _currentAdjutant.getActivePlayer();
        try {
            _log.info("Processing order for " + player + " of type " + order.getType());
            _currentAdjutant = order.execute(_game);
            if (_game.gameOver()) {
                handleGameOver();
            } else {
                _channels.publishAdjutant(_currentAdjutant);
            }
            if( !player.equals(_currentAdjutant)){
                // Save game state at the end of a players turn
                // it's hard to recover in the middle of a turn, so we do not try to
                doGameStateSave();
            }

        } catch (GameException ge) {
            _log.error("Error processing an order " + ge.getMessage() + " with " +  player + " with ai " + _automatedPlayers.get(player));
            throw ge;
        }
    }

    private void doGameStateSave(){
        GameState gameState = _game.getGameState();
        Map<Player, String> aiPlayerNames = new HashMap<>();
        for(Player ai : _automatedPlayers.keySet()){
            String playerName = ai.getDisplayName();
            aiPlayerNames.put(ai, playerName);
        }
        gameState.setAiPlayerNames(aiPlayerNames);
        GameSaver.saveGameState(gameState);
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
        if( ai != null ) {
            Instant start = Instant.now();
            Order order = ai.generateOrder(_currentAdjutant, _game);
            Instant end = Instant.now();
            Duration timeSpent = Duration.between(start, end);
            if(timeSpent.getSeconds() > 1.0){
                _log.warn("On turn " + _game.getTurnNumber() + " ai " + ai.getPlayer() + " took " + timeSpent
                    + " when choosing between " + adjutant.allowableOrders());
            }
            littleDelay();
            _channels.publishOrder(order);
        }
    }

    private void handleNoMoveReceivedEvent(NoMoveReceivedEvent event){
        _log.info("No move received for game: " + getGameId());
        Player activePlayer = _currentAdjutant.getActivePlayer();
        List<Player> players = Collections.singletonList(activePlayer);
        List<AutomatedPlayer> ais = _aiFactory.generateAiPlayers(players);
        AutomatedPlayer newAiPlayer = ais.get(0);
        newAiPlayer.initialize(_game);
        _automatedPlayers.put(activePlayer, newAiPlayer);
        _log.info("Player " + activePlayer + " became " + newAiPlayer);
        aiOrderGenerator(_currentAdjutant);
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

    private void startGame(GameStartRequest gameStartRequest){
        if ( _game != null ){
            // this is a restore situation
            _log.info("Restarting a restored game");

            _currentAdjutant = Adjutant.restoredGameAdjutant(_game);
            _channels.publishAdjutant(_currentAdjutant);
        } else {
            // this is a true start
            _game = initializeGame(gameStartRequest, _channels);
            _log.info("Game initialized");
            assignCountries();
            _log.info("Countries assigned");
            initializedAIs(_game);
            _game.start();
            _log.info("Game started");
            _game.publishAllState();
            _currentAdjutant = Adjutant.newGameAdjutant(_game.getGameId(), _game.currentAttacker());
            _log.info("New game adjutant created " + _game.getGameId() + ", " + _game.currentAttacker());
            _channels.publishAdjutant(_currentAdjutant);
            _log.info("Starting game " + gameStartRequest);
        }
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

    private Game initializeGame(GameStartRequest startRequest, Channels channels) {
        _log.info("Initializing game");
        int initialArmies = initialArmyCount(_colors.length);
        Tuple<List<Player>, Map<Player, AutomatedPlayer>> newPlayers = _preGame.newPlayers(_colors, _aiFactory);

        WorldMap map = new StandardMap();
        Roller roller = new RandomRoller(System.currentTimeMillis());
        _automatedPlayers = newPlayers.getSecond();
        GameId gameId = startRequest.getGameId();

        return new Game(gameId, map, newPlayers.getFirst(), StandardCardSet.deck, roller, channels, initialArmies);
    }

    private void assignCountries(){
        List<Country> countries = _game.getAllCountries();
        Collections.shuffle(countries);

        List<Player> players = _game.getAllPlayers();
        for(int idx=0; idx<countries.size(); idx++){
            Player player = players.get(idx%_colors.length);
            Country country = countries.get(idx);
            _game.processPlaceArmyOrder(player, country, 1, false);
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
