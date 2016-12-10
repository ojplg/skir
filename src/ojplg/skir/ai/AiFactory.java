package ojplg.skir.ai;

import ojplg.skir.state.Player;

import java.util.Random;

public class AiFactory {

    private static Random _random = new Random();

    public static AutomatedPlayer generateAiPlayer(Player player){
        float number = _random.nextFloat();

        if (number < 0.1){
            return new Wimpy(player);
        } else if (number < 0.7){
            return new Grabby(player);
        } else {
            return new Bully(player);
        }
    }
}
