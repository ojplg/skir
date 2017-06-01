package ojplg.skir.state.event;

import ojplg.skir.map.Country;
import ojplg.skir.state.Player;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

public class GameEvent {

    private final String _simpleText;
    private final Integer _turnNumber;
    private final GameEventType _gameEventType;
    private final List<String> _playerIdentifiers;

    private GameEvent(String text, GameEventType gameEventType, String playerIdentifier){
        _simpleText = text;
        _turnNumber = null;
        _gameEventType = gameEventType;
        _playerIdentifiers = Collections.singletonList(playerIdentifier);
    }

    private GameEvent(int turnNumber, List<String> playerIdentifiers){
        _simpleText = "Game was drawn";
        _playerIdentifiers = playerIdentifiers;
        _gameEventType = GameEventType.Draw;
        _turnNumber = turnNumber;
    }

    private GameEvent(String text, int turnNumber, GameEventType gameEventType, String playerIdentifier){
        _simpleText = text;
        _turnNumber = turnNumber;
        _gameEventType = gameEventType;
        _playerIdentifiers = Collections.singletonList(playerIdentifier);
    }

    public static GameEvent joinsGame(Player player){
        return new GameEvent(player.getColor() + " joins game", 0, GameEventType.PlayerJoins, player.getDisplayName());
    }

    public static GameEvent eliminated(Player player, int turnNumber){
        return new GameEvent(player.getColor() + " was eliminated", turnNumber, GameEventType.PlayerEliminated, player.getDisplayName());
    }

    public static GameEvent wins(Player player, int turnNumber){
        return new GameEvent(player.getColor() + " wins the game", turnNumber, GameEventType.Win, player.getDisplayName());
    }

    public static GameEvent draw(int turnNumber, List<String>  playerIdentifiers){
        return new GameEvent(turnNumber, playerIdentifiers);
    }

    public static GameEvent forAttack(Player player, Country fromCountry, Country toCountry){
        return new GameEvent(attackText(player, fromCountry, toCountry), GameEventType.Attack, player.getDisplayName());
    }

    public static GameEvent forOccupy(Player player, Country fromCountry, Country toCountry){
        return new GameEvent(occupyText(player, fromCountry, toCountry),GameEventType.Occupy, player.getDisplayName());
    }

    public static GameEvent forFortify(Player player, Country fromCountry, Country toCountry){
        return new GameEvent(fortifyText(player, fromCountry, toCountry),GameEventType.Fortify, player.getDisplayName());
    }

    public static GameEvent forCardExchange(Player player){
        return new GameEvent(player.getColor() + " exchanges cards", GameEventType.ExchangeCards, player.getDisplayName());
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

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "game_event");
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
