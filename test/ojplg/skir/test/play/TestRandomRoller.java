package ojplg.skir.test.play;

import ojplg.skir.play.RandomRoller;
import ojplg.skir.play.Rolls;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestRandomRoller {

    @Test
    public void testOneVOneExpectationsCalculationsWork(){
        for(int attacker=1; attacker<=3; attacker++){
            for(int defender=1; defender<=2; defender++){
                runExpectationsDifferenceTest(1000000, attacker, defender);
            }
        }
    }

    private void runExpectationsDifferenceTest(int numberOfTests, int attackingDiceCount, int defendingDiceCount){
        int allowableAmountOff = numberOfTests / 1000;
        RandomRoller randomRoller = new RandomRoller(System.currentTimeMillis());

        float accumulatedDifferenceFromAttackerExpectations = 0;
        float accumulatedDifferenceFromDefenderExpectations = 0;

        for(int idx= 0; idx<numberOfTests; idx++) {
            Rolls rolls = randomRoller.roll(attackingDiceCount, defendingDiceCount);
            accumulatedDifferenceFromAttackerExpectations += rolls.attackersExpectationsDifference();
            accumulatedDifferenceFromDefenderExpectations += rolls.defendersExpectationsDifference();
        }
        // These are probablistic tests ... after 10000 rolls, difference from expectations should be (relatively) small
        assertEquals("Problem with " + attackingDiceCount + " v " + defendingDiceCount, 0, accumulatedDifferenceFromAttackerExpectations, allowableAmountOff);
        assertEquals("Problem with " + attackingDiceCount + " v " + defendingDiceCount, 0, accumulatedDifferenceFromDefenderExpectations, allowableAmountOff);

        // This test is much more exact ... if the attacker was lucky the defender was unlucky
        assertEquals( accumulatedDifferenceFromAttackerExpectations, -accumulatedDifferenceFromDefenderExpectations, 0.01);
    }

    @Test
    public void testDistributionIsFlat(){
        int numberOfTests = 10000;
        int expectedNumberOfEachRoll = numberOfTests/6;
        int allowablePercentageOff = 10;
        int expectedCountEpsilon = expectedNumberOfEachRoll/allowablePercentageOff;

        RandomRoller randomRoller = new RandomRoller(System.currentTimeMillis());
        Map<Integer, Integer> histogram = new HashMap<>();
        int[] results = randomRoller.generateRolls(numberOfTests);
        for(int idx=0; idx<results.length; idx++){
            int roll = results[idx];
            if( roll < 1 || roll > 6){
                fail("Roll of " + roll + "found");
            }
            histogram.computeIfPresent(roll, (k, v) ->  1 + v );
            histogram.putIfAbsent(roll, 1);
        }

        for(int roll=1; roll<=6; roll++){
            int occurences = histogram.get(roll);
            assertTrue( "Found " + occurences + " of " + roll, occurences - expectedNumberOfEachRoll < expectedCountEpsilon);
        }
    }
}
