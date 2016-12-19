package ojplg.skir.state;

import ojplg.skir.map.Country;
import ojplg.skir.map.WorldMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Occupations {

    private final WorldMap _map;
    private final Map<Country, Force> _records = new HashMap<>();

    public Occupations(WorldMap map){
        _map = map;
    }

    public boolean isOccupied(Country country){
        return _records.containsKey(country);
    }

    public void killArmies(Country country, int count){
        _records.get(country).killArmies(count);
    }

    public boolean allArmiesDestroyed(Country country){
        if (isOccupied(country)){
            return  getOccupationForce(country) == 0;
        }
        throw new RuntimeException("Country " + country + " is unoccupied");
    }

    public int getOccupationForce(Country country){
        if( isOccupied(country) ){
            return _records.get(country).getArmies();
        }
        return 0;
    }

    public Player getOccupier(Country country){
        if( isOccupied(country) ){
            return _records.get(country).getPlayer();
        }
        return null;
    }

    public boolean hasAttackingForces(Country country){
        return _records.get(country).getArmies() > 1;
    }

    public boolean hasEnemyNeighbor(Country country){
        Player occupier = getOccupier(country);
        return _map.getNeighbors(country).stream()
                .anyMatch(c -> ! getOccupier(c).equals(occupier));
    }

    public List<Country> alliedNeighbors(Country country){
        Player occupier = getOccupier(country);
        return _map.getNeighbors(country).stream()
                .filter(c -> getOccupier(c).equals(occupier))
                .collect(Collectors.toList());
    }

    public List<Country> enemyNeighbors(Country country){
        Player occupier = getOccupier(country);
        return _map.getNeighbors(country).stream()
                .filter(c -> ! getOccupier(c).equals(occupier))
                .collect(Collectors.toList());
    }


    public List<Country> countriesOccupied(Player player){
        List<Country> occupied = new ArrayList<Country>();
        for ( Map.Entry<Country, Force> entry : _records.entrySet() ) {
            if ( player.equals(entry.getValue().getPlayer())) {
                occupied.add(entry.getKey());
            }
        }
        return occupied;
    }

    public int totalOccupationForces(Player player){
        int total = 0;
        for(Country country : countriesOccupied(player)){
            total += getOccupationForce(country);
        }
        return total;
    }

    public void placeArmies(Player player, Country country, int cnt){
        if ( ! isOccupied(country) ){
            _records.put(country, new Force(player, cnt));
            return;
        }
        Force existingForce = _records.get(country);
        if ( existingForce.getPlayer().equals(player) ){
            existingForce.addArmies(cnt);
        } else if ( existingForce.getArmies() > 0 ){
            throw new RuntimeException("Player " + player + " cannot put armies into country " + country
             + ". It is owned by " + existingForce.getPlayer());
        } else {
            _records.put(country, new Force(player, cnt));
        }
    }

    public WorldMap getMap(){
        return _map;
    }
}
