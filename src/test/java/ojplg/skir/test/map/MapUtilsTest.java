package ojplg.skir.test.map;

import ojplg.skir.map.Country;
import ojplg.skir.map.MapUtils;
import ojplg.skir.map.StandardMap;
import ojplg.skir.map.WorldMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapUtilsTest {

    @Test
    public void testFindShortestPathNeighbors(){
        WorldMap map = new StandardMap();
        List<Country> path = MapUtils.findShortestPath(map, Country.Afghanistan, Country.Middle_East);
        Country[] expectedPath = new Country[]{ Country.Afghanistan, Country.Middle_East};
        assertArrayEquals(expectedPath, path.toArray());
    }

    @Test
    public void testFindShortestPathNeighbors2(){
        WorldMap map = new StandardMap();
        List<Country> path = MapUtils.findShortestPath(map, Country.Eastern_Australia, Country.Western_Australia);
        Country[] expectedPath = new Country[]{ Country.Eastern_Australia, Country.Western_Australia};
        assertArrayEquals(expectedPath, path.toArray());
    }

    @Test
    public void testFindShortestPathLengthThreeNoOptions(){
        WorldMap map = new StandardMap();
        List<Country> path = MapUtils.findShortestPath(map, Country.Scandinavia, Country.Greenland);
        Country[] expectedPath = new Country[]{ Country.Scandinavia, Country.Iceland, Country.Greenland};
        assertArrayEquals(expectedPath, path.toArray());
    }

    @Test
    public void testFindShortestPathLengthFourNoOptions(){
        WorldMap map = new StandardMap();
        List<Country> path = MapUtils.findShortestPath(map, Country.Western_Australia, Country.China);
        Country[] expectedPath = new Country[]{ Country.Western_Australia, Country.Indonesia, Country.Siam, Country.China};
        assertArrayEquals(expectedPath, path.toArray());
    }

    @Test
    public void testFindShortestPathLengthFiveNoOptions(){
        WorldMap map = new StandardMap();
        List<Country> path = MapUtils.findShortestPath(map, Country.Brazil, Country.Alberta);
        Country[] expectedPath = new Country[]{ Country.Brazil, Country.Venezuela,
                Country.Central_America, Country.Western_United_States, Country.Alberta};
        assertArrayEquals(expectedPath, path.toArray());
    }

    @Test
    public void testFindShortestPathLength3Options(){
        WorldMap map = new StandardMap();
        List<Country> path = MapUtils.findShortestPath(map, Country.Northern_Europe, Country.North_Africa);
        assertEquals(3, path.size());
        assertEquals(Country.Northern_Europe, path.get(0));
        assertEquals(Country.North_Africa, path.get(2));
        assertTrue(path.get(1).equals(Country.Western_Europe) || path.get(1).equals(Country.Southern_Europe));
    }

    @Test
    public void testFindShortestPathLength4Options(){
        WorldMap map = new StandardMap();
        List<Country> path = MapUtils.findShortestPath(map, Country.Greenland, Country.Central_America);
        assertEquals(4, path.size());
        assertEquals(Country.Greenland, path.get(0));
        assertEquals(Country.Central_America, path.get(3));
        assertTrue(path.get(1).equals(Country.Quebec) || path.get(1).equals(Country.Ontario));
        assertTrue(path.get(2).equals(Country.Western_United_States) || path.get(2).equals(Country.Eastern_United_States));
    }

    @Test
    public void testFindShortestPathLength5Options() {
        WorldMap map = new StandardMap();
        List<Country> path = MapUtils.findShortestPath(map, Country.Great_Britain, Country.China);
        assertEquals(5, path.size());
    }

    @Test
    public void testIsContiguousBloc1(){
        WorldMap map = new StandardMap();
        List<Country> bloc = Arrays.asList(
                Country.Argentina, Country.Brazil, Country.Venezuela, Country.North_Africa, Country.Central_America, Country.Western_United_States);
        assertTrue(MapUtils.isContiguousBloc(map, bloc));
    }

    @Test
    public void testIsContiguousBloc2(){
        WorldMap map = new StandardMap();
        List<Country> bloc = Arrays.asList(
                Country.Argentina, Country.Brazil, Country.Venezuela, Country.North_Africa, Country.Central_America, Country.Quebec);
        assertFalse(MapUtils.isContiguousBloc(map, bloc));
    }

    @Test
    public void testIsContiguousBloc3(){
        WorldMap map = new StandardMap();
        List<Country> bloc = Arrays.asList(
                Country.Argentina, Country.Venezuela, Country.North_Africa, Country.Central_America, Country.Quebec);
        assertFalse(MapUtils.isContiguousBloc(map, bloc));
    }

    @Test
    public void testIsContiguousBloc4(){
        WorldMap map = new StandardMap();
        List<Country> bloc = Arrays.asList(
                Country.Great_Britain, Country.Iceland, Country.Greenland, Country.Scandinavia);
        assertTrue(MapUtils.isContiguousBloc(map, bloc));
    }


}
