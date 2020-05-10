package ojplg.skir.play.orders;

import ojplg.skir.map.Countries;
import ojplg.skir.map.Country;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

public class PlaceArmyConstraints implements OrderConstraints {

    private final int _maximumArmies;
    private final List<Country> _possibleCountries;

    public PlaceArmyConstraints(int _maximumArmies, List<Country> _possibleCountries) {
        this._maximumArmies = _maximumArmies;
        this._possibleCountries = Collections.unmodifiableList(_possibleCountries);
    }

    public int getMaximumArmies() {
        return _maximumArmies;
    }

    @Override
    public boolean allowableOrder(Order order) {
        if (!order.getType().equals(OrderType.PlaceArmy)) {
            return false;
        }
        PlaceArmy placeArmy = (PlaceArmy) order;
        int numberArmies = placeArmy.getCount();
        Country country = placeArmy.getCountry();

        if (numberArmies > _maximumArmies){
            return false;
        }
        return _possibleCountries.contains(country);
    }

    public List<Country> getPossibleCountries() {
        return _possibleCountries;
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject jObject = new JSONObject();
        jObject.put("maximum_armies", getMaximumArmies());
        jObject.put("possible_countries", Countries.asJsonArray(getPossibleCountries()));
        return jObject;
    }

    @Override
    public String toString() {
        return "PlaceArmyConstraints{" +
                "_maximumArmies=" + _maximumArmies +
                ", _possibleCountries=" + _possibleCountries +
                '}';
    }
}
