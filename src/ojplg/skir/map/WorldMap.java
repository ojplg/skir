package ojplg.skir.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WorldMap {

    private final List<Continent> _continents;
    private final Neighbors _neighbors;

    public WorldMap(Continent[] continents, Neighborhood[] neighborhoods){
        List<Continent> cons = Arrays.asList(continents);
        Collections.sort(cons);
        _continents = Collections.unmodifiableList(cons);
        _neighbors = new Neighbors(neighborhoods);
    }

    public List<Continent> getContinents(){
        return _continents;
    }

    public List<Country> getNeighbors(Country country){
        return _neighbors.getNeighbors(country);
    }

    public boolean areNeighbors(Country one, Country two){
        return _neighbors.areNeighbors(one, two);
    }

    public List<Country> getAllCountries(){
        List<Country> countries = new ArrayList<Country>();

        for (Continent con : getContinents()){
            countries.addAll(con.getCountries());
        }

        return countries;
    }

}
