package ojplg.skir.state.event;

import ojplg.skir.map.Country;
import ojplg.skir.state.Player;
import org.json.simple.JSONObject;

public class GameEvent {

    private final String _simpleText;

    private GameEvent(String text){
        _simpleText = text;
    }

    public static GameEvent joinsGame(Player player){
        return new GameEvent(player.getColor() + " joins game");
    }

    public static GameEvent eliminated(Player player){
        return new GameEvent(player.getColor() + " was eliminated");
    }

    public static GameEvent wins(Player player){
        return new GameEvent(player.getColor() + " wins the game");
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

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "game_event");
        jObject.put("simple_text", _simpleText);
        return jObject;
    }

    private static String attackText(Player player, Country fromCountry, Country toCountry){
        StringBuilder bldr = new StringBuilder();
        bldr.append(player.getColor());
        bldr.append(" attacks ");
        bldr.append(toCountry.getName());
        bldr.append(" from ");
        bldr.append(fromCountry.getName());
        return bldr.toString();
    }

    private static String occupyText(Player player, Country fromCountry, Country toCountry){
        StringBuilder bldr = new StringBuilder();
        bldr.append(player.getColor());
        bldr.append(" attacks ");
        bldr.append(toCountry.getName());
        bldr.append(" from ");
        bldr.append(fromCountry.getName());
        return bldr.toString();
    }

    private static String fortifyText(Player player, Country fromCountry, Country toCountry){
        StringBuilder bldr = new StringBuilder();
        bldr.append(player.getColor());
        bldr.append(" fortifies ");
        bldr.append(toCountry.getName());
        bldr.append(" from ");
        bldr.append(fromCountry.getName());
        return bldr.toString();
    }
}
