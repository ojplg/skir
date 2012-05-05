package map;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Continent implements Comparable {

    private final String _name;
    private final List<Country> _countries;
    private final int _bonus;

    public static final Continent Africa = new Continent("Africa", new Country[]{ Country.Egypt, Country.Congo,
            Country.East_Africa, Country.North_Africa, Country.South_Africa, Country.Madagascar }, 3 );

    public static final Continent Asia = new Continent("Asia", new Country[] { Country.Afghanistan,
            Country.China, Country.India, Country.Irkutsk, Country.Japan, Country.Kamchatka, Country.Middle_East,
            Country.Mongolia, Country.Siam, Country.Siberia, Country.Ural, Country.Yakutsk}, 7);

    public static final Continent Australia = new Continent("Australia", new Country[] { Country.Eastern_Australia,
            Country.Indonesia, Country.New_Guinea, Country.Western_Australia}, 2);

    public static final Continent Europe = new Continent("Europe", new Country[] { Country.Great_Britain,
            Country.Iceland, Country.Northern_Europe, Country.Scandinavia, Country.Southern_Europe, Country.Ukraine,
            Country.Western_Europe }, 5);

    public static final Continent North_America = new Continent("North America", new Country[] {Country.Alaska,
            Country.Alberta, Country.Central_America, Country.Eastern_United_States, Country.Greenland,
            Country.Northwest_Territory, Country.Ontario, Country.Quebec, Country.Western_United_States }, 5);

    public static final Continent South_America = new Continent("South America", new Country[] {Country.Argentina,
            Country.Brazil, Country.Peru, Country.Venezuela}, 2);

    public Continent(String name, Country[] countries, int bonus){
        _name = name;
        List<Country> cs = Arrays.asList(countries);
        Collections.sort(cs);
        _countries = Collections.unmodifiableList(cs);
        _bonus = bonus;
    }

    public List<Country> getCountries(){
        return _countries;
    }

    public int numberCountries(){
        return _countries.size();
    }

    public int getBonus(){
        return _bonus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Continent continent = (Continent) o;

        return (_name != null ? !_name.equals(continent._name) : continent._name != null);
    }

    @Override
    public int hashCode() {
        return _name != null ? _name.hashCode() : 0;
    }

    @Override
    public int compareTo(Object o) {
        Continent that = (Continent) o;
        return this._name.compareTo(that._name);
    }

    public String getName() {
        return _name;
    }
}
