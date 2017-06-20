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
        switch(name){
            case "Grabby": return new Grabby(player);
            case "Bully": return new Bully(player);
            case "Massy": return new Massy(player);
            case "Grumpy": return new Grumpy(player);
            case "Wimpy": return new Wimpy(player);
            case "AiTuney": return firstTuned(player);
            case "PsTuney": return presetTuned(player);
            //case "EvTuney": return evolveTuned(player, random.nextInt(30));
            case "EvTuney": return evolveTuned(player, 29);
            case "Ev2Tuney": return evolveTuned(player, "evolve200");
            case "TuneyAdditive1" : return evolvedAdditiveTuney(player, 1);
            case "TuneyAdditive47" : return evolvedAdditiveTuney(player, 47);
            case "TuneyAdditive64" : return evolvedAdditiveTuney(player, 64);
            case "TuneyAdditive81" : return evolvedAdditiveTuney(player, 81);
            default: return new Wimpy(player);
        }
    }

    public void setFirstPlayerFactory(Function<Player, AutomatedPlayer> firstPlayerFactory){
        this._firstPlayerFactory = firstPlayerFactory;
    }

    private String randomKey(){
        return RandomUtils.pickRandomElement(Arrays.asList(_eligibleAiNames));
    }

    private Tuney firstTuned(Player player) {
        try {
            String json = "{\"PostCardMinimumAttackScoreAttackKey\":0.263,\"MinimumAttackScoreAttackKey\":0.163,\"BorderCountryAndContinentBorderAndOwnedPlacementKey\":0.5,\"ContinentOwnedPlacementKey\":0.8538706489631215,\"AttackerArmyPercentageApplicationAttackKey\":0.24684478218139208,\"TotalEnemyRatioTestPlacementKey\":0.20615565922592521,\"GoalCountryNeighborPlacementKey\":0.9384065660400394,\"BorderCountryPlacementKey\":0.3994079673402347,\"ContinentalBorderPlacementKey\":0.824445756987892,\"NumberEnemyCountriesRatioApplicationPlacementKey\":0.7574274879878028,\"NumberEnemyCountriesRatioTestPlacementKey\":0.49018590987510907,\"LargestEnemyRatioTestPlacementKey\":0.5012023976181936,\"TotalEnemyRatioApplicationPlacementKey\":0.25781357714342085,\"AttackerArmyPercentageTestAttackKey\":0.3886617734830039,\"LargestEnemyRatioApplicationPlacementKey\":0.28730408608044056,\"TargetInBestGoalContinentAttackKey\":0.6730583013337731,\"ContinentBorderAndOwnedPlacementKey\":0.32115732746860803}";
            JSONParser parser = new JSONParser();
            Map<String, Double> jsonObject = (Map<String, Double>) parser.parse(json);
            return new Tuney(player, jsonObject, "AiTuney");
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }

    private Tuney evolveTuned(Player player, int number){
        String playerName = "ev1_" + String.format("%02d", number);
        return evolveTuned(player, playerName);
    }

    private Tuney evolvedAdditiveTuney(Player player, int number){
        Map<String,Double> tunings = tunings("add_" + number);
        return new Tuney(player, tunings, "TuneyAdditive" + number, false);

    }

    private Tuney evolveTuned(Player player, String playerName){
        try {
            JSONParser parser = new JSONParser();
            Map<String,Double> jsonObject = (Map<String,Double>) parser.parse(
                    new InputStreamReader(getClass().getResourceAsStream("/tunings/" + playerName + ".json")));
            return new Tuney(player, jsonObject, playerName);
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
        return new Tuney(player, Tuney.presetTunings(), "PsTuney");
    }
    
}
