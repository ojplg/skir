package ojplg.skir.ai;

import ojplg.skir.state.Player;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AiFactory {

    private static Map<String, Function<Player, AutomatedPlayer>> _playerGenerators;
    private static List<String> _playerNames;

    private Function<Player, AutomatedPlayer> _firstPlayerFactory;
    private List<String> _eligibleAiNames;

    static {
        _playerGenerators = playerGenerators();
        _playerNames = extractPlayerNames();
    }

    public AiFactory(List<String> aiNames){
        _eligibleAiNames = aiNames;
    }

    public static List<String> allPlayerNames(){
        return _playerNames;
    }

    public List<AutomatedPlayer> generateAiPlayers(List<Player> players){
        List<AutomatedPlayer> automatedPlayers = new ArrayList<>();
        for(Player player : players) {

            if (_firstPlayerFactory != null && player.getNumber() == 0) {
                automatedPlayers.add(_firstPlayerFactory.apply(player));
            } else {
                String name = RandomUtils.pickRandomElement(_eligibleAiNames);
                player.setDisplayName(name);
                automatedPlayers.add(_playerGenerators.get(name).apply(player));
            }
        }
        return automatedPlayers;
    }

    private static List<String> extractPlayerNames(){
        List<String> names  = new ArrayList();
        names.addAll(_playerGenerators.keySet());
        Collections.sort(names);
        return Collections.unmodifiableList(names);
    }

    private static Map<String, Function<Player, AutomatedPlayer>> playerGenerators(){
        Map<String, Function<Player, AutomatedPlayer>> generators = new HashMap();

        generators.put("Grabby", Grabby::new);
        generators.put("Bully", Bully::new);
        generators.put("Massy", Massy::new);
        generators.put("MassyTwo", MassyTwo::new);
        generators.put("Grumpy", Grumpy::new);
        generators.put("Wimpy", Wimpy::new);
        generators.put("WimpyTwo", WimpyTwo::new);
        generators.put("Tuney_MM", p -> new Tuney(p, Tuney.presetTunings()));
        generators.put("Tuney_M2", p -> evolvedTuney(p, "evolve2"));
        generators.put("Tuney_M200", p -> evolvedTuney(p, "evolve200"));
        generators.put("Tuney_A1", p -> evolvedAdditiveTuney(p, 1));
        generators.put("Tuney_A47", p -> evolvedAdditiveTuney(p, 47));
        generators.put("Tuney_A64", p -> evolvedAdditiveTuney(p, 64));
        generators.put("Tuney_A81", p -> evolvedAdditiveTuney(p, 81));
        generators.put("TuneyTwo", p -> new TuneyTwo(p, TuneyTwo.presetTunings()));
        generators.put("TT_46_38", p -> evolvedTuneyTwo(p,"tuney_two_46_38"));
        generators.put("TT_36_22", p -> evolvedTuneyTwo(p,"tuney_two_36_22"));

        return Collections.unmodifiableMap(generators);
    }

    public void setFirstPlayerFactory(Function<Player, AutomatedPlayer> firstPlayerFactory){
        this._firstPlayerFactory = firstPlayerFactory;
    }

    private static Tuney evolvedAdditiveTuney(Player player, int number){
        Map<String,Double> tunings = tunings("add_" + number);
        return new Tuney(player, tunings, false);

    }

    private static Tuney evolvedTuney(Player player, String fileName){
        Map<String,Double> tunings = tunings(fileName);
        return new Tuney(player, tunings);
    }

    private static TuneyTwo evolvedTuneyTwo(Player player, String fileName){
        return new TuneyTwo(player, tunings(fileName));
    }

    private static Map<String,Double> tunings(String fileName){
        try {
            JSONParser parser = new JSONParser();
            InputStreamReader reader = new InputStreamReader(
                    parser.getClass().getResourceAsStream("/tunings/" + fileName + ".json"));
            return (Map<String,Double>) parser.parse(reader);
        } catch (IOException | ParseException ex){
            throw new RuntimeException(ex);
        }
    }
}
