package ojplg.skir.test.utils;

import ojplg.skir.utils.RatioDistributor;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class RatioDistributorTest
{
    @Test
    public void testOneItem(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.5);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 4);

        assertEquals(1, distributed.size());
        assertEquals(new Integer(4), distributed.get("A"));
    }

    @Test
    public void testTwoEqualItems(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.3);
        ratios.put("B", 0.3);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 4);

        assertEquals(2, distributed.size());
        assertEquals(new Integer(2), distributed.get("A"));
        assertEquals(new Integer(2), distributed.get("B"));
    }

    @Test
    public void testTwoUnEqualItems(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.3);
        ratios.put("B", 0.9);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 4);

        assertEquals(2, distributed.size());
        assertEquals(new Integer(1), distributed.get("A"));
        assertEquals(new Integer(3), distributed.get("B"));
    }

    @Test
    public void testTwoUnEqualItemsOnlyOneToDistribute(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.3);
        ratios.put("B", 0.9);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 1);

        assertEquals(1, distributed.size());
        assertEquals(new Integer(1), distributed.get("B"));
    }

    @Test
    public void testThreeEqualItems(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 1.3);
        ratios.put("B", 1.3);
        ratios.put("C", 1.3);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 12);

        assertEquals(3, distributed.size());
        assertEquals(new Integer(4), distributed.get("A"));
        assertEquals(new Integer(4), distributed.get("B"));
        assertEquals(new Integer(4), distributed.get("A"));
    }


    @Test
    public void testThreeUnEqualItems(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.1);
        ratios.put("B", 0.9);
        ratios.put("C", 0.01);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 10);

        assertEquals(2, distributed.size());
        assertEquals(new Integer(1), distributed.get("A"));
        assertEquals(new Integer(9), distributed.get("B"));
    }

    @Test
    public void testThreeUnEqualItemsUnevenAmount(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.1);
        ratios.put("B", 0.9);
        ratios.put("C", 0.01);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 8);

        assertEquals(2, distributed.size());
        assertEquals(new Integer(1), distributed.get("A"));
        assertEquals(new Integer(7), distributed.get("B"));
    }

    @Test
    public void ratioDistributorAlwaysDistributesCorrectAmount(){
        Random random = new Random();
        for(int idx=0; idx<100; idx ++) {
            int amount = random.nextInt(200);
            int optionCount = random.nextInt(25) + 1;
            testPreservesAmountDistributedInvariant(amount, optionCount, random);
        }
    }

    private void testPreservesAmountDistributedInvariant(int amount, int optionCount, Random random){
        Map<Integer, Double> ratios = new HashMap<>();
        for(int idx=0; idx<optionCount; idx++){
            ratios.put(new Integer(idx), random.nextDouble());
        }
        Map<Integer,Integer> distributed = RatioDistributor.distribute(ratios, amount);
        int totalAmountDistributed = distributed.values().stream().reduce(0, (a,b) -> a+b);
        assertEquals("Failed for amount " + amount + " with " + optionCount + " options", amount, totalAmountDistributed );
    }

    @Test
    public void ratioDistributorRespectsMaxAmountOfOne(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 8.0);
        ratios.put("B", 3.0);
        ratios.put("C", 12.0);
        ratios.put("D", 6.0);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 5, 1);

        assertEquals(1, distributed.size());
        assertEquals(new Integer(5), distributed.get("C"));
    }

    @Test
    public void ratioDistributorRespectsMaxAmountOfTwo(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 8.0);
        ratios.put("B", 3.0);
        ratios.put("C", 12.0);
        ratios.put("D", 6.0);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 5, 2);

        assertEquals(2, distributed.size());
        assertEquals(new Integer(3), distributed.get("C"));
        assertEquals(new Integer(2), distributed.get("A"));
    }


}
