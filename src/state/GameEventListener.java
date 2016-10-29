package state;

import map.Country;

public interface GameEventListener {
    void mapChanged(Country country, Player player, int armyCount);
    void playerChanged(Player player, int armyCount, int countryCount);
    String getId();
}
