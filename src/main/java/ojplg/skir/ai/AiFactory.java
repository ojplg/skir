package ojplg.skir.ai;

import ojplg.skir.state.Constants;
import ojplg.skir.play.Skir;
import ojplg.skir.state.Player;
import org.jetlang.fibers.Fiber;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class AiFactory {

    private Function<Player, AutomatedPlayer> _firstPlayerFactory;
    private final Random random = new Random(System.currentTimeMillis());
    private final Fiber _fiber = Skir.createThreadFiber("AiFactoryFiber");
    private String[] _eligibleAiNames;

    public AiFactory(String[] aiNames){
        _eligibleAiNames = aiNames;
            _fiber.start();
    }

    public AutomatedPlayer generateAiPlayer(Player player){
        if( _firstPlayerFactory != null && player.getNumber() == 0){
            return _firstPlayerFactory.apply(player);
        }

        String name = randomKey();
        player.setDisplayName(name);
        switch(name){
            case "Grabby": return new Grabby(player);
            case "Bully": return new Bully(player);
            case "Massy": return new Massy(player);
            case "Grumpy": return new Grumpy(player);
            case "Wimpy": return new Wimpy(player);
            case "Tuney (MM)": return presetTuned(player);
            case "Tuney (M2)": return evolveTuned(player, "evolve2");
            case "Tuney (M200)": return evolveTuned(player, "evolve200");
            case "Tuney (A1)" : return evolvedAdditiveTuney(player, 1);
            case "Tuney (A47)" : return evolvedAdditiveTuney(player, 47);
            case "Tuney (A64)" : return evolvedAdditiveTuney(player, 64);
            case "Tuney (A81)" : return evolvedAdditiveTuney(player, 81);
            case "TuneyTwo" : return new TuneyTwo(player, TuneyTwo.presetTunings());
            default: return new Wimpy(player);
        }
    }

    public void setFirstPlayerFactory(Function<Player, AutomatedPlayer> firstPlayerFactory){
        this._firstPlayerFactory = firstPlayerFactory;
    }

    private String randomKey(){
        return RandomUtils.pickRandomElement(Arrays.asList(_eligibleAiNames));
    }

    private Tuney evolveTuned(Player player, int number){
        String playerName = "ev1_" + String.format("%02d", number);
        return evolveTuned(player, playerName);
    }

    private Tuney evolvedAdditiveTuney(Player player, int number){
        Map<String,Double> tunings = tunings("add_" + number);
        return new Tuney(player, tunings, false);

    }

    private Tuney evolveTuned(Player player, String fileName){
        try {
            JSONParser parser = new JSONParser();
            Map<String,Double> jsonObject = (Map<String,Double>) parser.parse(
                    new InputStreamReader(getClass().getResourceAsStream("/tunings/" + fileName + ".json")));
            return new Tuney(player, jsonObject);
        } catch (IOException io){
            throw new RuntimeException(io);
        } catch (ParseException pe){
            throw new RuntimeException(pe);
        }
    }

    private Map<String,Double> tunings(String fileName){
        try {
            JSONParser parser = new JSONParser();
            Map<String,Double> jsonObject = (Map<String,Double>) parser.parse(
                    new InputStreamReader(getClass().getResourceAsStream("/tunings/" + fileName + ".json")));
            return jsonObject;
        } catch (IOException io){
            throw new RuntimeException(io);
        } catch (ParseException pe){
            throw new RuntimeException(pe);
        }
    }

    private Tuney presetTuned(Player player){
        return new Tuney(player, Tuney.presetTunings());
    }
    
}
