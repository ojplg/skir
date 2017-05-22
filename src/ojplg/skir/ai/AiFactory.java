package ojplg.skir.ai;

import ojplg.skir.state.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.function.Function;

public class AiFactory {

    private final Random _random = new Random();

    private Function<Player, AutomatedPlayer> _firstPlayerFactory;

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
            default: return new Wimpy(player);
        }
    }

    public void setFirstPlayerFactory(Function<Player, AutomatedPlayer> firstPlayerFactory){
        this._firstPlayerFactory = firstPlayerFactory;
    }

    private String randomKey(){
        //String[] names = new String[] {"Grabby", "Bully", "Massy", "Grumpy" , "Wimpy", "AiTuney", "PsTuney" };
        String[] names = new String[] {"Bully", "Massy", "Grumpy", "PsTuney" };
        return RandomUtils.pickRandomElement(Arrays.asList(names));
    }

    private Tuner generateRandom(Player player){
        Map<String, Double> tunings = new HashMap<>();
        Tuner.tuningKeys().forEach(
                k -> tunings.put(k, _random.nextDouble())
        );
        return new Tuner(player, tunings, "Random");
    }
    
    private Tuner firstTuned(Player player) {
        try {
            String json = "{\"PostCardMinimumAttackScoreAttackKey\":0.263,\"MinimumAttackScoreAttackKey\":0.163,\"BorderCountryAndContinentBorderAndOwnedPlacementKey\":0.5,\"ContinentOwnedPlacementKey\":0.8538706489631215,\"AttackerArmyPercentageApplicationAttackKey\":0.24684478218139208,\"TotalEnemyRatioTestPlacementKey\":0.20615565922592521,\"GoalCountryNeighborPlacementKey\":0.9384065660400394,\"BorderCountryPlacementKey\":0.3994079673402347,\"ContinentalBorderPlacementKey\":0.824445756987892,\"NumberEnemyCountriesRatioApplicationPlacementKey\":0.7574274879878028,\"NumberEnemyCountriesRatioTestPlacementKey\":0.49018590987510907,\"LargestEnemyRatioTestPlacementKey\":0.5012023976181936,\"TotalEnemyRatioApplicationPlacementKey\":0.25781357714342085,\"AttackerArmyPercentageTestAttackKey\":0.3886617734830039,\"LargestEnemyRatioApplicationPlacementKey\":0.28730408608044056,\"TargetInBestGoalContinentAttackKey\":0.6730583013337731,\"ContinentBorderAndOwnedPlacementKey\":0.32115732746860803}";
            JSONParser parser = new JSONParser();
            Map<String, Double> jsonObject = (Map<String, Double>) parser.parse(json);
            return new Tuner(player, jsonObject, "AiTuney");
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
    }

    private Tuner presetTuned(Player player){
        return new Tuner(player, Tuner.presetTunings(), "PsTuney");
    }
    
}
