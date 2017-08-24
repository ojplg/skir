package ojplg.skir.state.event;

import ojplg.skir.map.Country;
import ojplg.skir.state.GameId;
import ojplg.skir.state.Player;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

/**
 * This class is a real horror show. The individual message types should each
 * have its own class.
 */
public class GameEvent implements GameEventMessage {

    private final String _simpleText;
    private final Integer _turnNumber;
    private final GameEventType _gameEventType;
    private final List<String> _playerIdentifiers;
    private final GameId _gameId;

    private GameEvent(GameId gameId, String text, GameEventType gameEventType, String playerIdentifier){
        _simpleText = text;
        _turnNumber = null;
        _gameEventType = gameEventType;
        _playerIdentifiers = Collections.singletonList(playerIdentifier);
        _gameId = gameId;
    }

    private GameEvent(GameId gameId, int turnNumber, List<String> playerIdentifiers){
        _simpleText = "Game was drawn";
        _playerIdentifiers = playerIdentifiers;
        _gameEventType = GameEventType.Draw;
        _turnNumber = turnNumber;
        _gameId = gameId;
    }

    private GameEvent(GameId gameId, String text, int turnNumber, GameEventType gameEventType, String playerIdentifier){
        _simpleText = text;
        _turnNumber = turnNumber;
        _gameEventType = gameEventType;
        _playerIdentifiers = Collections.singletonList(playerIdentifier);
        _gameId = gameId;
    }

    public static GameEvent joinsGame(GameId gameId, Player player){
        return new GameEvent(gameId, player.getColor() + " joins game", 0, GameEventType.PlayerJoins, player.getDisplayName());
    }

    public static GameEvent eliminated(GameId gameId,Player player, int turnNumber){
        return new GameEvent(gameId,player.getColor() + " was eliminated", turnNumber, GameEventType.PlayerEliminated, player.getDisplayName());
    }

    public static GameEvent wins(GameId gameId,Player player, int turnNumber){
        return new GameEvent(gameId,player.getColor() + " wins the game", turnNumber, GameEventType.Win, player.getDisplayName());
    }

    public static GameEvent draw(GameId gameId,int turnNumber, List<String>  playerIdentifiers){
        return new GameEvent(gameId,turnNumber, playerIdentifiers);
    }

    public static GameEvent forAttack(GameId gameId,Player player, Country fromCountry, Country toCountry){
        return new GameEvent(gameId,attackText(player, fromCountry, toCountry), GameEventType.Attack, player.getDisplayName());
    }

    public static GameEvent forOccupy(GameId gameId,Player player, Country fromCountry, Country toCountry){
        return new GameEvent(gameId,occupyText(player, fromCountry, toCountry),GameEventType.Occupy, player.getDisplayName());
    }

    public static GameEvent forFortify(GameId gameId,Player player, Country fromCountry, Country toCountry){
        return new GameEvent(gameId,fortifyText(player, fromCountry, toCountry),GameEventType.Fortify, player.getDisplayName());
    }

    public int getTurnNumber(){
        return _turnNumber;
    }

    public GameEventType getGameEventType(){
        return _gameEventType;
    }

    public String getPlayerIdentifier(){
        return _playerIdentifiers.get(0);
    }

    public List<String> getPlayerIdentifiers(){
        return _playerIdentifiers;
    }

    public boolean isGameOver(){
        return _gameEventType.equals(GameEventType.Draw)
                || _gameEventType.equals(GameEventType.Win);
    }

    @Override
    public GameId getGameId() {
        return _gameId;
    }

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "game_event");
        jObject.put("game_id", _gameId.getId());
        jObject.put("simple_text", _simpleText);
        jObject.put("turn_number", _turnNumber);
        jObject.put("game_over", _gameEventType == GameEventType.Draw || _gameEventType == GameEventType.Win);
        return jObject;
    }

    private static String attackText(Player player, Country fromCountry, Country toCountry){
        return toFromText(player, "attacks", fromCountry, toCountry);
    }

    private static String occupyText(Player player, Country fromCountry, Country toCountry){
        return toFromText(player, "occupies", fromCountry, toCountry);
    }

    private static String fortifyText(Player player, Country fromCountry, Country toCountry){
        return toFromText(player, "fortifies", fromCountry, toCountry);
    }

    private static String toFromText(Player player, String verb, Country fromCountry, Country toCountry){
        StringBuilder bldr = new StringBuilder();
        bldr.append(player.getColor());
        bldr.append(" ");
        bldr.append(verb);
        bldr.append(" ");
        bldr.append(toCountry.getName());
        bldr.append(" from ");
        bldr.append(fromCountry.getName());
        return bldr.toString();
    }
}
