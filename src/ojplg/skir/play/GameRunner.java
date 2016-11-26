package ojplg.skir.play;

import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.ai.Bully;
import ojplg.skir.ai.NeverAttacks;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.card.StandardCardSet;
import ojplg.skir.map.Country;
import ojplg.skir.map.StandardMap;
import ojplg.skir.map.WorldMap;
import ojplg.skir.state.event.JoinGameRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;
import ojplg.skir.state.ClientInfo;
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

    private final Map<Player,AutomatedPlayer> _automatedPlayers = new HashMap<Player,AutomatedPlayer>();
    private final Map<Player, ClientInfo> _remotePlayerInfo = new HashMap<Player, ClientInfo>();
    private final Game _game;
    private final Channels _channels;
    private final Fiber _fiber;

    private Adjutant _currentAdjutant;
    private boolean _gameStarted = false;

    private final Set<JoinGameRequest> _joinRequests = new HashSet<>();

    public GameRunner(Channels channels, Fiber fiber){
        _channels = channels;
        _fiber = fiber;
        _game = initializeGame(channels);

        _channels.OrderEnteredChannel.subscribe(_fiber,
                order -> processOrder(order));
        _channels.ClientConnectedEventChannel.subscribe(_fiber,
                clientConnectedEvent -> handleClientConnection(clientConnectedEvent));
        _channels.JoinGameRequestChannel.subscribe(_fiber,
                joinGameRequest -> { _joinRequests.add(joinGameRequest); });
        _channels.StartGameChannel.subscribe(_fiber,
                s -> startGame(s));
    }

    public static List<String> getColors(){
        return Collections.unmodifiableList(Arrays.asList(_colors));
    }

    private void initializeAutomatedPlayers(){
        for (int idx = 1; idx < _colors.length; idx++) {
            Player player = _game.getAllPlayers().get(idx);
            AutomatedPlayer ai;
            if ( idx % 2 == 0 ) {
                ai = new NeverAttacks(player);
            } else {
                ai = new Bully(player);
            }
            addAutomatedPlayer(ai);
        }
    }

    private void handleClientConnection(ClientConnectedEvent clientConnectedEvent){

        int availablePlayerNumber = _remotePlayerInfo.size();

        if( playerSlotAvailable()) {
            _gameStarted = true;
            Player player = _game.getAllPlayers().get(availablePlayerNumber);

            ClientInfo clientInfo = new ClientInfo(clientConnectedEvent, player);

            _remotePlayerInfo.put(player, clientInfo);
            player.setClientKey(clientInfo.getClientKey());

            _log.info("Player " + availablePlayerNumber + " who is " + player.getColor());

            GameJoinedEvent gameJoinedEvent = new GameJoinedEvent(
                    clientConnectedEvent, player, availablePlayerNumber == 0);
            _channels.GameJoinedEventChannel.publish(gameJoinedEvent);
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
        AutomatedPlayer ai = getAutomatedPlayer(_currentAdjutant.getActivePlayer());
        if( ai != null ){
            Order generatedOrder = ai.generateOrder(_currentAdjutant, _game);
            processOrder(generatedOrder);
        } else {
            _channels.AdjutantChannel.publish(_currentAdjutant);
        }
    }
    
    private void startGame(String s){
        _log.info("Starting game " + s);
        assignCountries();
        initializeAutomatedPlayers();
        _game.start();
        _game.publishAllState();
        _currentAdjutant = Adjutant.nextPlayer(_game.currentAttacker());
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
        List<Player> players = new ArrayList<Player>();
        int initialArmies = initialArmyCount(_colors.length);
        for (int idx = 0; idx < _colors.length; idx++) {
            Player player = new Player(_colors[idx]);
            player.grantReserves(initialArmies);
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
