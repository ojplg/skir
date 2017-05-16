package ojplg.skir.ai;

import ojplg.skir.state.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class AiFactory {

    private final Random _random = new Random();

    private Function<Player, AutomatedPlayer> _firstPlayerFactory;

    public AutomatedPlayer generateAiPlayer(Player player){
        if( _firstPlayerFactory != null && player.getNumber() == 0){
            return _firstPlayerFactory.apply(player);
        }

//        return new Grumpy(player);

        float number = _random.nextFloat();

        if( number < 0.25) {
            return new Massy(player);
        } else if (number < 0.5){
            return new Grumpy(player);
        } else if (number < 0.75){
            return new Grabby(player);
        } else if (number < 0.9 ){
            return new Bully(player);
        } else {
            return new Wimpy(player);
        }
    }

    public void setFirstPlayerFactory(Function<Player, AutomatedPlayer> firstPlayerFactory){
        this._firstPlayerFactory = firstPlayerFactory;
    }

    private Tuner generateRandom(Player player){
        Map<String, Double> tunings = new HashMap<>();
        Tuner.tuningKeys().forEach(
                k -> tunings.put(k, _random.nextDouble())
        );
        return new Tuner(player, tunings, "Random");
    }
}
