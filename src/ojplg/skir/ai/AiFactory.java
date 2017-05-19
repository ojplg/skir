package ojplg.skir.ai;

import ojplg.skir.state.Player;

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
            default: return new Wimpy(player);
        }
    }

    public void setFirstPlayerFactory(Function<Player, AutomatedPlayer> firstPlayerFactory){
        this._firstPlayerFactory = firstPlayerFactory;
    }

    private String randomKey(){
        String[] names = new String[] {"Grabby", "Bully", "Massy", "Grumpy" , "Wimpy" };
        return RandomUtils.pickRandomElement(Arrays.asList(names));
    }

    private Tuner generateRandom(Player player){
        Map<String, Double> tunings = new HashMap<>();
        Tuner.tuningKeys().forEach(
                k -> tunings.put(k, _random.nextDouble())
        );
        return new Tuner(player, tunings, "Random");
    }
}
