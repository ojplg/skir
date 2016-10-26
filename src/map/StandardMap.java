package map;

public class StandardMap extends WorldMap {

    private static final Neighborhood[] graph = new Neighborhood[]{
            set(Country.Afghanistan, Country.Ural, Country.China, Country.Ukraine, Country.India, Country.Middle_East ),
            set(Country.Alaska, Country.Kamchatka, Country.Northwest_Territory, Country.Alberta),
            set(Country.Alberta, Country.Northwest_Territory, Country.Ontario, Country.Western_United_States, Country.Alaska),
            set(Country.Argentina, Country.Peru, Country.Brazil),
            set(Country.Brazil, Country.Venezuela, Country.North_Africa, Country.Argentina, Country.Peru),
            set(Country.Central_America, Country.Western_United_States, Country.Eastern_United_States, Country.Venezuela),
            set(Country.China, Country.Siberia, Country.Mongolia, Country.Siam, Country.India, Country.Afghanistan),
            set(Country.Congo, Country.North_Africa, Country.East_Africa, Country.South_Africa),
            set(Country.East_Africa, Country.Egypt, Country.Middle_East, Country.Madagascar, Country.South_Africa, Country.Congo, Country.North_Africa),
            set(Country.Eastern_Australia,Country.Indonesia, Country.Western_Australia),
            set(Country.Eastern_United_States, Country.Ontario, Country.Quebec, Country.Central_America, Country.Western_United_States),
            set(Country.Egypt, Country.Southern_Europe, Country.Middle_East, Country.East_Africa, Country.North_Africa),
            set(Country.Great_Britain, Country.Iceland, Country.Scandinavia, Country.Northern_Europe, Country.Western_Europe),
            set(Country.Greenland, Country.Iceland, Country.Quebec, Country.Ontario, Country.Northwest_Territory),
            set(Country.Iceland, Country.Greenland, Country.Scandinavia, Country.Great_Britain),
            set(Country.India, Country.Afghanistan, Country.China, Country.Siam, Country.Middle_East),
            set(Country.Indonesia, Country.Siam, Country.Western_Australia, Country.New_Guinea ),
            set(Country.Irkutsk, Country.Siberia, Country.Yakutsk, Country.Kamchatka, Country.Mongolia),
            set(Country.Japan, Country.Kamchatka, Country.Mongolia),
            set(Country.Kamchatka, Country.Alaska, Country.Japan, Country.Mongolia, Country.Irkutsk, Country.Yakutsk),
            set(Country.Madagascar, Country.East_Africa, Country.South_Africa),
            set(Country.Middle_East, Country.Ukraine, Country.Afghanistan,Country.India, Country.East_Africa, Country.Egypt, Country.Southern_Europe),
            set(Country.Mongolia, Country.Siberia, Country.Irkutsk, Country.Kamchatka, Country.Japan, Country.China),
            set(Country.New_Guinea, Country.Eastern_Australia, Country.Western_Australia, Country.Indonesia),
            set(Country.North_Africa, Country.Western_Europe, Country.Southern_Europe, Country.Egypt, Country.East_Africa, Country.Congo, Country.Brazil),
            set(Country.Northern_Europe, Country.Scandinavia, Country.Ukraine, Country.Southern_Europe, Country.Western_Europe, Country.Great_Britain),
            set(Country.Northwest_Territory, Country.Greenland, Country.Quebec, Country.Ontario, Country.Alberta, Country.Alaska),
            set(Country.Ontario, Country.Northwest_Territory, Country.Greenland, Country.Quebec, Country.Eastern_United_States, Country.Western_United_States),
            set(Country.Peru, Country.Venezuela, Country.Brazil, Country.Argentina),
            set(Country.Quebec, Country.Greenland, Country.Eastern_United_States, Country.Ontario, Country.Northwest_Territory),
            set(Country.Scandinavia, Country.Ukraine, Country.Northern_Europe, Country.Great_Britain, Country.Iceland),
            set(Country.Siam, Country.China, Country.Indonesia, Country.India),
            set(Country.Siberia, Country.Yakutsk, Country.Irkutsk, Country.Mongolia, Country.China, Country.Ural),
            set(Country.South_Africa, Country.East_Africa, Country.Madagascar, Country.Congo),
            set(Country.Southern_Europe, Country.Northern_Europe, Country.Ukraine, Country.Middle_East, Country.Egypt, Country.North_Africa, Country.Western_Europe),
            set(Country.Ukraine, Country.Ural, Country.Afghanistan, Country.Middle_East, Country.Southern_Europe, Country.Northern_Europe, Country.Scandinavia),
            set(Country.Ural, Country.Siberia, Country.China, Country.Afghanistan, Country.Ukraine),
            set(Country.Venezuela, Country.Central_America, Country.Brazil, Country.Peru),
            set(Country.Western_Australia, Country.New_Guinea, Country.Eastern_Australia),
            set(Country.Western_Europe, Country.Great_Britain, Country.Northern_Europe, Country.Southern_Europe, Country.North_Africa),
            set(Country.Western_United_States, Country.Alberta, Country.Ontario, Country.Eastern_United_States, Country.Central_America),
            set(Country.Yakutsk, Country.Kamchatka, Country.Irkutsk, Country.Siberia)
    };

    private static Neighborhood set(Country home, Country ... neighbors){
        return new Neighborhood(home, neighbors);
    }

    private static final Continent[] continents = new Continent[]{
            Continent.Africa,
            Continent.Asia,
            Continent.Australia,
            Continent.Europe,
            Continent.North_America,
            Continent.South_America
    };

    public StandardMap(){
        super(continents, graph);
    }
}
