package ojplg.skir.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MapUtils {

    public static List<Country> findShortestPath(WorldMap map, Country start, Country destination) {
        Map<Country, List<Country>> bestPaths = new HashMap<>();
        List<Country> neighbors = map.getNeighbors(start);
        for (Country neighbor : neighbors) {
            List<Country> path = Arrays.asList(start, neighbor);
            bestPaths.put(neighbor, path);
        }
        return findShortestPath(map, destination, bestPaths);
    }

    private static List<Country> findShortestPath(WorldMap map, Country destination, Map<Country, List<Country>> bestPaths) {
        if( bestPaths.containsKey(destination)){
            return bestPaths.get(destination);
        }
        List<Country> endPoints = new ArrayList<>(bestPaths.keySet());
        for(Country endPoint : endPoints){
            List<Country> path = bestPaths.get(endPoint);
            for(Country neighbor : map.getNeighbors(endPoint)){
                List<Country> newPath = append(path, neighbor);
                List<Country> oldPath = bestPaths.get(neighbor);
                if( oldPath == null){
                    bestPaths.put(neighbor, newPath);
                } else if (newPath.size()<oldPath.size()){
                    bestPaths.put(neighbor, newPath);
                }
            }
        }
        return findShortestPath(map, destination, bestPaths);
    }

    public static boolean bordersContinent(WorldMap map, Country country, List<Continent> continents){
        for(Continent continent : continents){
            if( map.isBorderToContinent(country, continent) ){
                return true;
            }
        }
        return false;
    }

    private static <T> List<T> append(List<T> list, T item){
        List<T> newList = new ArrayList<>(list);
        newList.add(item);
        return newList;
    }

    public static boolean isContiguousBloc(WorldMap map, List<Country> countries){
        // an empty collection is contiguous, so is a singleton collection
        if( countries.size() <= 1 ){
            return true;
        }

        SortedSet<Country> countriesToCheck = new TreeSet<>();
        Set<Country> checkedNotInBloc = new HashSet<>();
        Set<Country> checkedInBloc = new HashSet<>();

        Country start = countries.get(0);
        checkedInBloc.add(start);
        countriesToCheck.add(start);

        while(countriesToCheck.size() > 0){
            Country country = countriesToCheck.first();
            countriesToCheck.remove(country);
            Collection<Country> neighbors = map.getNeighbors(country);
            for(Country neighbor : neighbors){
                if( countries.contains(neighbor) && (! checkedInBloc.contains(neighbor))){
                    countriesToCheck.add(neighbor);
                    checkedInBloc.add(neighbor);
                } else {
                    checkedNotInBloc.add(neighbor);
                }
            }
        }

        return countries.size() == checkedInBloc.size();
    }
}
