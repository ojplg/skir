package map;

import org.json.simple.JSONArray;

import java.util.List;

public class Countries {

    public static JSONArray asJsonArray(List<Country> countries){
        JSONArray array = new JSONArray();
        for(Country country : countries) {
            array.add(country.getName());
        }
        return array;
    }
}
