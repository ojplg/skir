package ojplg.skir.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Neighborhood {

    private final Country _country;
    private final List<Country> _neighbors;

    public Neighborhood(Country country, Country[] neighbors){
        _country = country;
        _neighbors = Collections.unmodifiableList(Arrays.asList(neighbors));
    }

    public Country getCountry(){
        return _country;
    }

    public boolean isNeighbor(Country somewhere){
        return _neighbors.contains(somewhere);
    }

    public List<Country> getNeighbors(){
        return _neighbors;
    }
}
