package ojplg.skir.ai;

import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.state.Constants;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.utils.ListUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

public class AiUtils {

    static Continent findStrongestUnownedContinent(Player player, Game game){
        Map<Continent, Float> countryPercentages = computeCountryPercentages(player, game);
        Map<Continent, Float> armyPercentages = computeArmyPercentages(player, game);

        Continent best = null;
        float highest = 0;
        for(Continent continent : game.getAllContinents()){
            Float total = countryPercentages.get(continent) + armyPercentages.get(continent);
            if( total > 1.9999999 ){
                continue;
            }
            if( total > highest ){
                best = continent;
                highest = total;
            }
        }
        return best;
    }

    private static Map<Continent, Float> computeCountryPercentages(Player player, Game game){
        Map<Continent, Float> ownedPercentages = new HashMap<>();
        for(Continent continent : game.getAllContinents()){
            Float percentage = computeCountryPercentage(player, continent, game);
            ownedPercentages.put(continent, percentage);
        }
        return ownedPercentages;
    }

    private static Map<Continent, Float> computeArmyPercentages(Player player, Game game){
        Map<Continent, Float> ownedPercentages = new HashMap<>();
        for(Continent continent : game.getAllContinents()){
            Float percentage = computeArmyPercentage(player, continent, game);
            ownedPercentages.put(continent, percentage);
        }
        return ownedPercentages;
    }

    private static Float computeCountryPercentage(Player player, Continent continent, Game game){
        float countryCount = 0;
        float ownedCount = 0;
        for(Country country : continent.getCountries()){
            countryCount++;
            if( player.equals(game.getOccupier(country))){
                ownedCount++;
            }
        }
        return ownedCount/countryCount;
    }

    private static Float computeArmyPercentage(Player player, Continent continent, Game game){
        float armyCount = 0;
        float myArmyCount = 0;
        for(Country country : continent.getCountries()){
            int force = game.getOccupationForce(country);
            armyCount += force;
            if( player.equals(game.getOccupier(country))){
                myArmyCount += force;
            }
        }
        return myArmyCount/armyCount;
    }

    /**
     * Finds all the possible places where the player has more armies
     * than a neighboring enemy occupied country.
     */
    static List<PossibleAttack> findAdvantageousAttacks(Player player, Game game){
        return findAdvantageousAttacks(player, game, 0);
    }

    static List<PossibleAttack> findAdvantageousAttacks(Player player, Game game, int minimumAmount){
        List<PossibleAttack> possibilities = findAllPossibleAttacks(player, game);
        return ListUtils.filter(possibilities, p -> p.getAdvantage() > minimumAmount);
    }

    static PossibleAttack findBestPossibleAttack(Player player, Game game){
        List<PossibleAttack> advantageousAttacks = findAdvantageousAttacks(player, game);
        if( advantageousAttacks.isEmpty()){
            return null;
        }
        return advantageousAttacks.stream().max(
                (pa, pb) -> { return pa.getAdvantage() -  pb.getAdvantage();} ).get();
    }

    static List<PossibleAttack> findAllPossibleAttacks(Player player, Game game){
        List<PossibleAttack> possibilities = new ArrayList<>();
        for(Country country : game.findCountriesToAttackFrom(player)){
            int myForce = game.getOccupationForce(country);
            for(Country enemyNeighbor : game.findEnemyNeighbors(country)){
                int enemyForce = game.getOccupationForce(enemyNeighbor);
                possibilities.add(new PossibleAttack(country, enemyNeighbor, myForce , enemyForce));
            }
        }
        return possibilities;
    }

    static int computeTotalOccupyingForces(List<Country> countries, Game game){
        return countries.stream()
                .map(game::getOccupationForce)
                .reduce(0, (a, b) -> a + b);
    }

    static int highestOccupyingForce(List<Country> countries, Game game){
        Optional<Integer> possibleHighest = countries.stream()
                .map(game::getOccupationForce)
                .max(Integer::compareTo);
        if( possibleHighest.isPresent()){
            return possibleHighest.get();
        }
        return 0;
    }

    public static List<Continent> enemyOwnedContinents(Player player, Game game){
        List<Continent> ownedContinents = game.findOwnedContinents();
        return ListUtils.filter(ownedContinents, c -> ! game.isContinentOwner(player, c));
    }

    static Country findStrongestPossession(Player player, Game game){
        List<Country> possessions = game.findOccupiedCountries(player);
        return possessions.stream()
                .max((c1, c2) -> (game.getOccupationForce(c1) - game.getOccupationForce(c2)))
                .get();
    }

    static Country findWeakestPossession(Player player, Game game){
        List<Country> possessions = game.findOccupiedCountries(player);
        return possessions.stream()
                .min((c1, c2) -> (game.getOccupationForce(c1) - game.getOccupationForce(c2)))
                .get();
    }

    static List<Country> findEnemyBorders(Player player, Game game){
        List<PossibleAttack> possibleAttacks = findAllPossibleAttacks(player, game);
        Set<Country> defendingCountries = possibleAttacks.stream().map(p -> p.getDefender()).collect(Collectors.toSet());
        return new ArrayList<>(defendingCountries);
    }

    static int attackingDice(Game game, Country country){
        return Math.min(Constants.MAXIMUM_ATTACKER_DICE,
                game.getOccupationForce(country) - 1);
    }

    static boolean isBorderCountry(Player player, Game game, Country country){
        return game.findBorderCountries(player).contains(country);
    }
}
