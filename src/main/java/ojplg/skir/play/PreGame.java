package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.state.GameId;
import ojplg.skir.state.Player;
import ojplg.skir.state.event.ClientConnectedEvent;
import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.GameJoinedEvent;
import ojplg.skir.utils.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreGame {

    private static final Logger _log = LogManager.getLogger(PreGame.class);

    private final Channels _channels;
    private GameId _gameId;

    private final Map<ClientConnectedEvent, Player> _connectedPlayers = new HashMap<>();

    public PreGame(Channels channels){
        _channels = channels;
        _gameId = GameId.next();
    }

    public boolean handleClientConnection(ClientConnectedEvent clientConnectedEvent){

        _log.info("Client connected " + clientConnectedEvent);

        if (_connectedPlayers.containsKey(clientConnectedEvent)){
            Player player = _connectedPlayers.get(clientConnectedEvent);
            _log.info("Player rejoined " + clientConnectedEvent + ", " + player);
            GameJoinedEvent gameJoinedEvent = new GameJoinedEvent(
                    clientConnectedEvent, player, false);
            _channels.GameJoinedEventChannel.publish(gameJoinedEvent);
            return true;
        } else if ( "demo".equalsIgnoreCase(clientConnectedEvent.getDisplayName()) ) {
            _log.info("Demo");
        } else if ( _connectedPlayers.size() < 6 ) {
            _log.info("Trying to add a new player " + clientConnectedEvent);
            int playerNumber = _connectedPlayers.size();
            String color = GameRunner.colorForIndex(playerNumber);
            Player player = new Player(color, playerNumber);

            _connectedPlayers.put(clientConnectedEvent, player);
            player.setClientKey(clientConnectedEvent.getClientKey());
            player.setDisplayName(clientConnectedEvent.getDisplayName());

            _log.info("Player " + playerNumber + " who is " + player.getColor());

            GameJoinedEvent gameJoinedEvent = new GameJoinedEvent(
                    clientConnectedEvent, player, playerNumber == 0);
            _channels.GameJoinedEventChannel.publish(gameJoinedEvent);
            _channels.GameEventChannel.publish(GameEvent.joinsGame(_gameId, player));
            _log.info("Published game joined event " + gameJoinedEvent);
        } else {
            _log.info("Could not join the game " + clientConnectedEvent);
        }
        return false;
    }

    public GameId getGameId(){
        return GameId.next();
    }

    public void next(){
        _gameId = GameId.next();
    }

    public Tuple<List<Player>, Map<Player,AutomatedPlayer>> newPlayers(String[] colors, AiFactory aiFactory){
        List<Player> players = new ArrayList<>();
        Map<Player, AutomatedPlayer> aiPlayers = new HashMap<>();
        for(Player player : _connectedPlayers.values()){
            players.add(player);
        }
        for (int idx = _connectedPlayers.size(); idx < colors.length; idx++) {
            Player player = new Player(colors[idx], idx);
            players.add(player);
            AutomatedPlayer ai = aiFactory.generateAiPlayer(player);
            aiPlayers.put(player, ai);
            _channels.GameEventChannel.publish(GameEvent.joinsGame(_gameId, player));
        }
        return new Tuple(players, aiPlayers);
    }
}
