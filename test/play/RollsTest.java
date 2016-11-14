package play;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RollsTest {

    @Test
    public void testSimpleAttackerWin(){
        Rolls rolls = new Rolls(new int[]{5}, new int[]{3});
        assertEquals(1, rolls.defendersLosses());
        assertEquals(0, rolls.attackersLosses());
    }

    @Test
    public void testSimpleAttackerLoss(){
        Rolls rolls = new Rolls(new int[]{2}, new int[]{3});
        assertEquals(0, rolls.defendersLosses());
        assertEquals(1, rolls.attackersLosses());
    }

    @Test
    public void testSplit(){
        Rolls rolls = new Rolls(new int[]{6,2}, new int[]{3,4});
        assertEquals(1, rolls.defendersLosses());
        assertEquals(1, rolls.attackersLosses());
    }

    @Test
    public void testTwoAttackerWins(){
        Rolls rolls = new Rolls(new int[]{5,4}, new int[]{3,1});
        assertEquals(2, rolls.defendersLosses());
        assertEquals(0, rolls.attackersLosses());
    }

    @Test
    public void testTwoDefenderWins(){
        Rolls rolls = new Rolls(new int[]{2,3}, new int[]{4,4});
        assertEquals(0, rolls.defendersLosses());
        assertEquals(2, rolls.attackersLosses());
    }

    @Test
    public void testTwoAttackerWinsIgnoreLowest(){
        Rolls rolls = new Rolls(new int[]{4,1,5}, new int[]{2,3});
        assertEquals(2, rolls.defendersLosses());
        assertEquals(0, rolls.attackersLosses());
    }

    @Test
    public void testTieGoesToDefender(){
        Rolls rolls = new Rolls(new int[]{4,1,3}, new int[]{4});
        assertEquals(0, rolls.defendersLosses());
        assertEquals(1, rolls.attackersLosses());
    }

    @Test
    public void testDifferenceFromExpectationsOneVersusOne(){
        Rolls rolls = new Rolls(new int[]{4}, new int[]{3});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(0.583, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(-0.583, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpectationsThreeVersusTwo(){
        Rolls rolls = new Rolls(new int[]{4,5,6}, new int[]{6,6});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(-1.079, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(1.079, defenderDifference, 0.001);
    }

}
