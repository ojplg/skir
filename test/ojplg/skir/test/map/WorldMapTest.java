package ojplg.skir.test.map;

import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.map.StandardMap;
import ojplg.skir.map.WorldMap;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WorldMapTest {

    @Test
    public void testIsBorderToContinent(){

        WorldMap map = new StandardMap();

        boolean doesBorder = map.isBorderToContinent(Country.Central_America, Continent.South_America);

        assertTrue(doesBorder);
    }
}
