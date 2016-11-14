package play;

import ai.AutomatedPlayer;
import ai.NeverAttacks;
import card.StandardCardSet;
import map.Country;
import map.StandardMap;
import map.WorldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import play.orders.Adjutant;
import play.orders.Order;
import play.orders.OrderType;
import state.ClientInfo;
import state.Game;
import state.Player;
import state.event.ClientConnectedEvent;
import state.event.GameJoinedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public GameRunner(Channels channels, Fiber fiber){
        _channels = channels;
        _fiber = fiber;
        _game = initializeGame(channels);

        _channels.OrderEnteredChannel.subscribe(_fiber,
                new Callback<Order>() {
                    @Override
                    public void onMessage(Order order) {
                        processOrder(order);
                    }
                });
        _channels.ClientConnectedEventChannel.subscribe(_fiber,
                new Callback<ClientConnectedEvent>() {
                    @Override
                    public void onMessage(ClientConnectedEvent clientConnectedEvent) {
                        handleClientConnection(clientConnectedEvent);
                    }
                });
    }

    private void initializeAutomatedPlayers(){
        for (int idx = 1; idx < _colors.length; idx++) {
            Player player = _game.getAllPlayers().get(idx);
            AutomatedPlayer ai = new NeverAttacks(player);
            addAutomatedPlayer(ai);
        }
    }

    private void handleClientConnection(ClientConnectedEvent clientConnectedEvent){

        // TODO: need to count this
        int availablePlayerNumber = 0;

        if( playerSlotAvailable()) {
            _gameStarted = true;
            Player player = _game.getAllPlayers().get(availablePlayerNumber);

            ClientInfo clientInfo = new ClientInfo(clientConnectedEvent, player);

            _remotePlayerInfo.put(player, clientInfo);
            player.setClientKey(clientInfo.getClientKey());

            _log.info("Player " + availablePlayerNumber + " who is " + player.getColor() + " has address " + clientConnectedEvent.getClientAddress());

            _channels.GameJoinedEventChannel.publish(new GameJoinedEvent(clientConnectedEvent.getClientKey(), player));

            // this should happen when a start command comes from client
            // when we know how to automate remaining players
            initializeAutomatedPlayers();
        } else {
            _log.info("Could not join the game " + clientConnectedEvent);
        }

        _channels.AdjutantChannel.publish(_currentAdjutant);
    }

    private boolean playerSlotAvailable(){
        return ! _gameStarted;
    }

    private void processOrder(Order order){
        _log.info("Processing order for " + _currentAdjutant.getActivePlayer() + " of type " + order.getType());
        _currentAdjutant = order.execute(_game);
        AutomatedPlayer ai = getAutomatedPlayer(_currentAdjutant.getActivePlayer());
        if( ai != null ){
            OrderType ot = ai.pickOrderType(_currentAdjutant.allowableOrders(), _game);
            Order generatedOrder = ai.generateOrder(ot, _currentAdjutant, _game);
            processOrder(generatedOrder);
            sillyDelay();
        } else {
            _channels.AdjutantChannel.publish(_currentAdjutant);
        }
    }

    private void sillyDelay(){
        try {
            Thread.sleep(100);
        } catch(InterruptedException ie){
            _log.error("Who interrupted me?", ie);
        }
    }

    public void startGame(){
        assignCountries();


        _currentAdjutant = Adjutant.nextPlayer(_game.currentAttacker());
        _channels.AdjutantChannel.publish(_currentAdjutant);
    }

    public void addAutomatedPlayer(AutomatedPlayer ai){
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
