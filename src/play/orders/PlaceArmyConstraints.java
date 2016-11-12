package play.orders;

import map.Country;
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

    public List<Country> getPossibleCountries() {
        return _possibleCountries;
    }

    @Override
    public JSONObject toJsonObject() {
        return null;
    }

    @Override
    public String toString() {
        return "PlaceArmyConstraints{" +
                "_maximumArmies=" + _maximumArmies +
                ", _possibleCountries=" + _possibleCountries +
                '}';
    }
}
