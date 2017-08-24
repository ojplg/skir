package ojplg.skir.test.play;

import ojplg.skir.play.Rolls;
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
    public void testDifferenceFromExpecationsOneVersusOneAttackerWins(){
        Rolls rolls = new Rolls(new int[]{4}, new int[]{3});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(0.583, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(-0.583, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpecationsOneVersusOneDefenderWins(){
        Rolls rolls = new Rolls(new int[]{3}, new int[]{3});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(-0.416, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(0.416, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpecationsTwoVersusOneAttackerWins(){
        Rolls rolls = new Rolls(new int[]{1,4}, new int[]{3});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(0.421, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(-0.421, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpecationsTwoVersusOneDefenderWins(){
        Rolls rolls = new Rolls(new int[]{3,2}, new int[]{3});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(-0.578, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(0.578, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpecationsThreeVersusOneAttackerWins(){
        Rolls rolls = new Rolls(new int[]{1,4,5}, new int[]{3});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(0.340, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(-0.340, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpecationsThreeVersusOneDefenderWins(){
        Rolls rolls = new Rolls(new int[]{3,2,2}, new int[]{3});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(-0.659, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(0.659, defenderDifference, 0.001);
    }


    @Test
    public void testDifferenceFromExpectationsThreeVersusTwoAttackerWinsTwo(){
        Rolls rolls = new Rolls(new int[]{4,5,6}, new int[]{3,2});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(0.92, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(-0.92, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpectationsThreeVersusTwoSplit(){
        Rolls rolls = new Rolls(new int[]{4,5,6}, new int[]{6,1});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(-0.079, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(0.079, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpectationsThreeVersusTwoDefenderWinsTwo(){
        Rolls rolls = new Rolls(new int[]{4,5,6}, new int[]{6,6});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(-1.079, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(1.079, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpectationsTwoVersusTwoAttackerWinsTwo(){
        Rolls rolls = new Rolls(new int[]{4,5}, new int[]{3,2});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(1.22, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(-1.22, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpectationsTwoVersusTwoSplit(){
        Rolls rolls = new Rolls(new int[]{5,6}, new int[]{6,1});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(0.22, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(-0.22, defenderDifference, 0.001);
    }

    @Test
    public void testDifferenceFromExpectationsTwoVersusTwoDefenderWinsTwo(){
        Rolls rolls = new Rolls(new int[]{5,6}, new int[]{6,6});
        double attackerDifference = rolls.attackersExpectationsDifference();
        assertEquals(-0.779, attackerDifference, 0.001);
        double defenderDifference = rolls.defendersExpectationsDifference();
        assertEquals(0.779, defenderDifference, 0.001);
    }


}
