package ojplg.skir.play.orders;

import ojplg.skir.map.Country;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FortificationConstraints implements OrderConstraints {

    private final Map<Country, Integer> _availableArmies;
    private final Map<Country, List<Country>> _availableDestinations;

    public FortificationConstraints(Player player, Game game){
        Map<Country, Integer> armies = new HashMap<Country, Integer>();
        Map<Country, List<Country>> destinations = new HashMap<Country, List<Country>>();
        List<Country> availableCountries = game.possibleFortificationCountries(player);
        for(Country country : availableCountries){
            int count = game.getOccupationForce(country) - 1;
            List<Country> alliedNeighbors = game.findAlliedNeighbors(country);
            armies.put(country, count);
            destinations.put(country, alliedNeighbors);
        }
        _availableArmies = Collections.unmodifiableMap(armies);
        _availableDestinations = Collections.unmodifiableMap(destinations);
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject jObject = new JSONObject();
        JSONObject counts = new JSONObject();
        for(Country country : _availableArmies.keySet()){
            counts.put(country.getName(), _availableArmies.get(country));
        }
        JSONObject destinations = new JSONObject();
        for(Country country : _availableDestinations.keySet()){
            List<Country> neighbors = _availableDestinations.get(country);
            JSONArray array = new JSONArray();
            for(Country neighbor : neighbors){
                array.add(neighbor.getName());
            }
            destinations.put(country.getName(), array);
        }
        jObject.put("counts", counts);
        jObject.put("destinations", destinations);
        return jObject;
    }
}
