package ojplg.skir.test.utils;

import ojplg.skir.utils.RatioDistributor;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class RatioDistributorTest
{
    @Test
    public void testOneItem(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.5);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 4);

        Assert.assertEquals(1, distributed.size());
        Assert.assertEquals(new Integer(4), distributed.get("A"));
    }

    @Test
    public void testTwoEqualItems(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.3);
        ratios.put("B", 0.3);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 4);

        Assert.assertEquals(2, distributed.size());
        Assert.assertEquals(new Integer(2), distributed.get("A"));
        Assert.assertEquals(new Integer(2), distributed.get("B"));
    }

    @Test
    public void testTwoUnEqualItems(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.3);
        ratios.put("B", 0.9);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 4);

        Assert.assertEquals(2, distributed.size());
        Assert.assertEquals(new Integer(1), distributed.get("A"));
        Assert.assertEquals(new Integer(3), distributed.get("B"));
    }

    @Test
    public void testTwoUnEqualItemsOnlyOneToDistribute(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.3);
        ratios.put("B", 0.9);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 1);

        Assert.assertEquals(1, distributed.size());
        Assert.assertEquals(new Integer(1), distributed.get("B"));
    }

    @Test
    public void testThreeEqualItems(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 1.3);
        ratios.put("B", 1.3);
        ratios.put("C", 1.3);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 12);

        Assert.assertEquals(3, distributed.size());
        Assert.assertEquals(new Integer(4), distributed.get("A"));
        Assert.assertEquals(new Integer(4), distributed.get("B"));
        Assert.assertEquals(new Integer(4), distributed.get("A"));
    }


    @Test
    public void testThreeUnEqualItems(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.1);
        ratios.put("B", 0.9);
        ratios.put("C", 0.01);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 10);

        Assert.assertEquals(2, distributed.size());
        Assert.assertEquals(new Integer(1), distributed.get("A"));
        Assert.assertEquals(new Integer(9), distributed.get("B"));
    }

    @Test
    public void testThreeUnEqualItemsUnevenAmount(){
        Map<String, Double> ratios = new HashMap<>();
        ratios.put("A", 0.1);
        ratios.put("B", 0.9);
        ratios.put("C", 0.01);

        Map<String, Integer> distributed = RatioDistributor.distribute(ratios, 8);

        Assert.assertEquals(2, distributed.size());
        Assert.assertEquals(new Integer(1), distributed.get("A"));
        Assert.assertEquals(new Integer(7), distributed.get("B"));
    }

}
