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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameRunner {

    private final static Logger _log = LogManager.getLogger(GameRunner.class);
    private final static String[] _colors = new String[]{ "Black", "Blue" , "Red", "Green", "White", "Pink"};

    private final Map<Player,AutomatedPlayer> _automatedPlayers = new HashMap<>();
    private final Map<ClientConnectedEvent, Player> _remotePlayerInfo = new HashMap<>();
    private final Set<Player> _remotePlayers = new HashSet<>();
    private final Game _game;
    private final Channels _channels;
    private final Fiber _fiber;

    private Adjutant _currentAdjutant;
    private boolean _gameStarted = false;

    public GameRunner(Channels channels, Fiber fiber){
        _channels = channels;
        _fiber = fiber;
        _game = initializeGame(channels);

        _channels.OrderEnteredChannel.subscribe(_fiber,
                order -> processOrder(order));
        _channels.ClientConnectedEventChannel.subscribe(_fiber,
                clientConnectedEvent -> handleClientConnection(clientConnectedEvent));
        _channels.StartGameChannel.subscribe(_fiber,
                s -> startGame(s));
        _channels.AdjutantChannel.subscribe(_fiber,
                a -> aiOrderGenerator(a));
    }

    public static List<String> getColors(){
        return Collections.unmodifiableList(Arrays.asList(_colors));
    }

    private void addAutomatedPlayers(){
        for (int idx = 0; idx < _colors.length; idx++) {
            Player player = _game.getAllPlayers().get(idx);
            if(! _remotePlayers.contains(player)) {
                AutomatedPlayer ai = AiFactory.generateAiPlayer(player);
                addAutomatedPlayer(ai);
                ai.initialize(_game);
            }
        }
    }

    private void handleClientConnection(ClientConnectedEvent clientConnectedEvent){

        _log.info("Client connected " + clientConnectedEvent);

        int availablePlayerNumber = _remotePlayerInfo.size();

        if (_remotePlayerInfo.containsKey(clientConnectedEvent)){
            Player player = _remotePlayerInfo.get(clientConnectedEvent);
            _log.info("Player rejoined " + clientConnectedEvent + ", " + player);
            _game.publishAllState();
            GameJoinedEvent gameJoinedEvent = new GameJoinedEvent(
                    clientConnectedEvent, player, false);
            _channels.GameJoinedEventChannel.publish(gameJoinedEvent);
            _channels.AdjutantChannel.publish(_currentAdjutant);
        } else if( playerSlotAvailable()) {
            _log.info("Trying to add a new player " + clientConnectedEvent);
            Player player = _game.getAllPlayers().get(availablePlayerNumber);
            //ClientInfo clientInfo = new ClientInfo(clientConnectedEvent, player);

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
        _log.info("Processing order for " + _currentAdjutant.getActivePlayer() + " of type " + order.getType());
        _currentAdjutant = order.execute(_game);
        if( _game.gameOver() ){
            _log.info("Game over. Winner is " + _game.currentAttacker());
            return;
        }
        _channels.AdjutantChannel.publish(_currentAdjutant);
    }

    private void aiOrderGenerator(Adjutant adjutant){
        AutomatedPlayer ai = getAutomatedPlayer(adjutant.getActivePlayer());

        if( ai != null ){
            Order order = ai.generateOrder(_currentAdjutant, _game);
            littleDelay();
            _channels.OrderEnteredChannel.publish(order);
        }
    }

    private void littleDelay(){
        try {
            Thread.sleep(25);
        } catch (InterruptedException ie){
            _log.warn("Who interrupted me?", ie);
        }
    }

    private void startGame(String s){
        _log.info("Starting game " + s);
        assignCountries();
        addAutomatedPlayers();
        _game.start();
        _gameStarted = true;
        _game.publishAllState();
        _currentAdjutant = Adjutant.nextPlayer(_game.currentAttacker(), _game.getTurnNumber());
        _channels.AdjutantChannel.publish(_currentAdjutant);
    }

    private void addAutomatedPlayer(AutomatedPlayer ai){
        _log.info("Adding automated player for " + ai.getPlayer());
        _automatedPlayers.put(ai.getPlayer(),ai);
    }

    public AutomatedPlayer getAutomatedPlayer(Player player){
        AutomatedPlayer ai = _automatedPlayers.get(player);
        if( ai == null ){
            _log.info("NO AI FOR " + player);
        }
        return ai;
    }

    private Game initializeGame(Channels channels) {
        List<Player> players = new ArrayList<>();
        int initialArmies = initialArmyCount(_colors.length);
        for (int idx = 0; idx < _colors.length; idx++) {
            Player player = new Player(_colors[idx]);
            if( idx == 0 ){
                player.grantReserves(7);
            } else {
                player.grantReserves(initialArmies);
            }
            players.add(player);
        }

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
            _game.placeArmy(player, country);
        }
        _game.doInitialPlacements();
    }

    private int initialArmyCount(int numberPlayers){
        return 20 + (5 * (6 - numberPlayers));
    }

}
