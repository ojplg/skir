package play;

import state.Rolls;

import java.util.Random;

public class RandomRoller implements Roller {

    private final Random _random;

    public RandomRoller(long seed){
        _random = new Random(seed);
    }

    public Rolls roll(int numberAttackers, int numberDefenders){
        int[] attackerRolls = generateRolls(numberAttackers);
        int[] defenderRolls = generateRolls(numberDefenders);

        return new Rolls(attackerRolls, defenderRolls);
    }

    private int[] generateRolls(int number){
        int[] rolls = new int[number];

        for( int idx=0; idx<number ; idx++ ){
            rolls[idx] = next();
        }

        return rolls;
    }

    private int next(){
        return 1 + (_random.nextInt() % 6);
    }
}
