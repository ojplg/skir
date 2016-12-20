package ojplg.skir.state.event;

import ojplg.skir.map.Country;
import ojplg.skir.state.Player;
import org.json.simple.JSONObject;

public class GameEvent {

    private final String _simpleText;
    private final Integer _turnNumber;

    private GameEvent(String text){
        _simpleText = text;
        _turnNumber = null;
    }

    private GameEvent(String text, int turnNumber){
        _simpleText = text;
        _turnNumber = turnNumber;
    }

    public static GameEvent joinsGame(Player player){
        return new GameEvent(player.getColor() + " joins game", 0);
    }

    public static GameEvent eliminated(Player player, int turnNumber){
        return new GameEvent(player.getColor() + " was eliminated", turnNumber);
    }

    public static GameEvent wins(Player player, int turnNumber){
        return new GameEvent(player.getColor() + " wins the game", turnNumber);
    }

    public static GameEvent draw(int turnNumber){
        return new GameEvent("Game was drawn", turnNumber);
    }

    public static GameEvent forAttack(Player player, Country fromCountry, Country toCountry){
        return new GameEvent(attackText(player, fromCountry, toCountry));
    }

    public static GameEvent forOccupy(Player player, Country fromCountry, Country toCountry){
        return new GameEvent(occupyText(player, fromCountry, toCountry));
    }

    public static GameEvent forFortify(Player player, Country fromCountry, Country toCountry){
        return new GameEvent(fortifyText(player, fromCountry, toCountry));
    }

    public static GameEvent forCardExchange(Player player){
        return new GameEvent(player.getColor() + " exchanges cards");
    }

    public int getTurnNumber(){
        return _turnNumber;
    }

    public boolean isMajorEvent(){
        return _turnNumber != null;
    }

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "game_event");
        jObject.put("simple_text", _simpleText);
        jObject.put("turn_number", _turnNumber);
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
