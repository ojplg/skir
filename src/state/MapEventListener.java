package state;

import map.Country;

public interface MapEventListener {
    void mapChanged(Country country, Player player, int armyCount);
    String getId();
}
