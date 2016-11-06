package map;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestStandardMap {

    @Test
    public void neighborsAreSymmetric(){
        StandardMap map = new StandardMap();
        List<Country> countryList = map.getAllCountries();
        for(Country country : countryList){

            List<Country> neighbors = map.getNeighbors(country);
            for(Country neighbor : neighbors){
                assertTrue(map.getNeighbors(neighbor).contains(country));
            }
        }
    }
}
