package ojplg.skir.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
