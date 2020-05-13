package ojplg.skir.map;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neighbors implements Serializable {

    private final Map<Country, Neighborhood> _neighborhoods;

    public Neighbors(Neighborhood[] hoods){
        Map<Country, Neighborhood> tmp = new HashMap<>();
        for( Neighborhood hood : hoods ){
            tmp.put(hood.getCountry(), hood);
        }
        _neighborhoods = Collections.unmodifiableMap(tmp);
    }

    public boolean areNeighbors(Country one, Country two){
        return _neighborhoods.get(one).isNeighbor(two);
    }

    public List<Country> getNeighbors(Country country){
        return _neighborhoods.get(country).getNeighbors();
    }

}
