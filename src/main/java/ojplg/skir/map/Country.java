package ojplg.skir.map;

public class Country implements Comparable {

    // Africa
    public static final Country Congo = new Country("Congo");
    public static final Country East_Africa = new Country("East Africa");
    public static final Country Egypt = new Country("Egypt");
    public static final Country Madagascar = new Country("Madagascar");
    public static final Country North_Africa = new Country("North Africa");
    public static final Country South_Africa = new Country("South Africa");

    // Asia
    public static final Country Afghanistan = new Country("Afghanistan");
    public static final Country China = new Country("China");
    public static final Country India = new Country("India");
    public static final Country Irkutsk = new Country("Irkutsk");
    public static final Country Japan = new Country("Japan");
    public static final Country Kamchatka = new Country("Kamchatka");
    public static final Country Middle_East = new Country("Middle East");
    public static final Country Mongolia = new Country("Mongolia");
    public static final Country Siam = new Country("Siam");
    public static final Country Siberia = new Country("Siberia");
    public static final Country Ural = new Country("Ural");
    public static final Country Yakutsk = new Country("Yakutsk");

    // Australia
    public static final Country Eastern_Australia = new Country("Eastern Australia");
    public static final Country Indonesia = new Country("Indonesia");
    public static final Country New_Guinea = new Country("New Guinea");
    public static final Country Western_Australia = new Country("Western Australia");

    //Europe
    public static final Country Great_Britain = new Country("Great Britain");
    public static final Country Iceland = new Country("Iceland");
    public static final Country Northern_Europe = new Country("Northern Europe");
    public static final Country Scandinavia = new Country("Scandinavia");
    public static final Country Southern_Europe = new Country("Southern Europe");
    public static final Country Ukraine = new Country("Ukraine");
    public static final Country Western_Europe = new Country("Western Europe");

    // North America
    public static final Country Alaska = new Country("Alaska");
    public static final Country Alberta = new Country("Alberta");
    public static final Country Central_America = new Country("Central America");
    public static final Country Eastern_United_States = new Country("Eastern United States");
    public static final Country Greenland = new Country("Greenland");
    public static final Country Northwest_Territory = new Country("Northwest Territory");
    public static final Country Ontario = new Country("Ontario");
    public static final Country Quebec = new Country("Quebec");
    public static final Country Western_United_States = new Country("Western United States");

    // South America
    public static final Country Argentina = new Country("Argentina");
    public static final Country Brazil = new Country("Brazil");
    public static final Country Peru = new Country("Peru");
    public static final Country Venezuela = new Country("Venezuela");

    private final String _name;

    public Country(String name){
        _name = name;
    }

    public String getName(){
        return _name;
    }

    @Override
    public String toString() {
        return "C." + _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        return _name != null ? _name.equals(country._name) : country._name == null;
    }

    @Override
    public int hashCode() {
        return _name != null ? _name.hashCode() : 0;
    }

    @Override
    public int compareTo(Object o) {
        Country that = (Country) o;
        return this._name.compareTo(that._name);
    }
}
