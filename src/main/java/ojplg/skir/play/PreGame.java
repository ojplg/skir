package ojplg.skir.play;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.state.Player;
import ojplg.skir.state.event.ClientConnectedEvent;
import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.GameJoinedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreGame {

    private static final Logger _log = LogManager.getLogger(PreGame.class);

    private final Channels _channels;

    private final Map<ClientConnectedEvent, Player> _connectedPlayers = new HashMap<>();

    public PreGame(Channels channels){
        _channels = channels;
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
            _channels.GameEventChannel.publish(GameEvent.joinsGame(player));
            _log.info("Published game joined event " + gameJoinedEvent);
        } else {
            _log.info("Could not join the game " + clientConnectedEvent);
        }
        return false;
    }

    public List<Player> newPlayers(String[] colors, int initialArmies, AiFactory aiFactory){
        List<Player> players = new ArrayList<>();
        for(Player player : _connectedPlayers.values()){
            player.grantReserves(initialArmies);
            players.add(player);
        }
        for (int idx = _connectedPlayers.size(); idx < colors.length; idx++) {
            Player player = new Player(colors[idx], idx);
            player.grantReserves(initialArmies);
            players.add(player);

            AutomatedPlayer ai = aiFactory.generateAiPlayer(player);
            player.setAutomatedPlayer(ai);
            _channels.GameEventChannel.publish(GameEvent.joinsGame(player));
        }
        return players;
    }
}