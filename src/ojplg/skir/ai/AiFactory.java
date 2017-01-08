package ojplg.skir.ai;

import ojplg.skir.state.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AiFactory {

    private static Random _random = new Random();

    public static AutomatedPlayer generateAiPlayer(Player player){
        float number = _random.nextFloat();

        if( number < 0.5) {
            return new Massy(player);
        } else if (number < 0.75){
            return new Grumpy(player);
        } else {
            return new Grabby(player);
        }

    }

    private static Tuner generateRandom(Player player){
        Map<String, Double> tunings = new HashMap<>();
        Tuner.tuningKeys().forEach(
                k -> tunings.put(k, _random.nextDouble())
        );
        return new Tuner(player, tunings, "Random");
    }
}
