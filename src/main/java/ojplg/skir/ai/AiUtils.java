package ojplg.skir.ai;

import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.map.MapUtils;
import ojplg.skir.state.Constants;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

public class AiUtils {

    public static Continent findStrongestUnownedContinent(Player player, Game game){
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
        Optional<PossibleAttack> best = ListUtils.findMax(advantageousAttacks);
        if( best.isPresent()){
            return best.get();
        }
        return null;
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

    public static boolean isEnemyOwnedContinent(Player player, Game game, Continent continent){
        return enemyOwnedContinents(player, game).contains(continent);
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

    public static Set<Country> findEnemyBorders(Player player, Game game){
        Set<Country> enemyNeighbors = new HashSet<>();
        for(Country country : game.findBorderCountries(player)){
            for(Country neighbor : game.findEnemyNeighbors(country)) {
                enemyNeighbors.add(neighbor);
            }
        }
        return enemyNeighbors;
    }

    public static List<Country> findEnemyBordersAsList(Player player, Game game){
        List<Country> list = new ArrayList<>();
        list.addAll(findEnemyBorders(player, game));
        return list;
    }


    static int attackingDice(Game game, Country country){
        return Math.min(Constants.MAXIMUM_ATTACKER_DICE,
                game.getOccupationForce(country) - 1);
    }

    static boolean isBorderCountry(Player player, Game game, Country country){
        return game.findBorderCountries(player).contains(country);
    }

    public static List<Continent> possibleGoalContinents(Player player, Game game,
                                                         double minArmyPercentage, double minCountryPercentage){
        List<Continent> goals = new ArrayList<>();
        for(Continent continent : game.getAllContinents()){
            double armyPercentage = continentalArmyPercentage(player, game, continent);
            double countryPercentage = continentalCountryPercentage(player, game, continent);
            if( ! (countryPercentage > 0.9999) &&
                    (armyPercentage > minArmyPercentage || countryPercentage > minCountryPercentage )){
                goals.add(continent);
            }
        }
        return goals;
    }

    public static double continentalArmyPercentage(Player player, Game game, Continent continent){
        double playerArmyCount = 0;
        double enemyArmyCount = 0;
        for(Country country : continent.getCountries()){
            int force = game.getOccupationForce(country);
            if( player.equals(game.getOccupier(country))){
                playerArmyCount += force;
            } else {
                enemyArmyCount += force;
            }
        }
        return playerArmyCount / (enemyArmyCount + playerArmyCount);
    }

    static double continentalCountryPercentage(Player player, Game game, Continent continent){
        double playerCountryCount = 0;
        double enemyCountryCount = 0;
        for(Country country : continent.getCountries()){
            if( player.equals(game.getOccupier(country))){
                playerCountryCount++;
            } else {
                enemyCountryCount++;
            }
        }
        return playerCountryCount / (enemyCountryCount + playerCountryCount);
    }

    static int unownedCountryCount(Player player, Game game, Continent continent){
        int count = 0;
        for(Country country : continent.getCountries()){
            if( ! player.equals(game.getOccupier(country))){
                count++;
            }
        }
        return count;
    }

    public static Set<Country> findContiguousOwnedCountries(Game game,Country base){
        Set<Country> checked = new HashSet<>();
        Set<Country> bloc = new HashSet<>();

        List<Country> toCheck = new ArrayList<>();
        toCheck.add(base);

        while (toCheck.size() > 0){
            Country country = toCheck.get(0);
            checked.add(country);
            bloc.add(country);
            for(Country neighbor : game.findAlliedNeighbors(country)){
                if( ! checked.contains(neighbor)){
                    toCheck.add(neighbor);
                }
            }
            toCheck.remove(country);
        }
        return bloc;
    }

    public static int findStrengthOfCountries(Game game, Collection<Country> countries){
        int strength = 0;
        for(Country country:countries){
            strength += game.getOccupationForce(country);
        }
        return strength;
    }

    public static List<Country> findInteriorCountriesWithExcessArmies(Game game, Player player){
        return ListUtils.filter(game.findInteriorCountries(player), (c -> game.getOccupationForce(c)>1));
    }

    /**
     * Computes the total value of all armies in all countries the player owns.
     */
    static int findAllPlayerArmies(Game game, Player player){
        int armies = 0;
        for(Country country : game.findOccupiedCountries(player)){
            armies += game.getOccupationForce(country);
        }
        return armies;
    }

    /**
     * Computes the total value of all armies in all countries the player
     * does not own.
     */
    public static int findAllOppositionArmies(Game game, Player player){
        int armies = 0;
        for(Country country : game.findEnemyOccupiedCountries(player)){
            armies += game.getOccupationForce(country);
        }
        return armies;
    }


    public static Double averageArmyStrength(Game game){
        return averageStrength(game, game.getAllCountries());
    }

    public static Double myAverageStrength(Game game, Player player){
        return averageStrength(game, game.findOccupiedCountries(player));
    }

    public static Double enemyAverageStrength(Game game, Player player){
        return averageStrength(game, game.findEnemyOccupiedCountries(player));
    }

    private static Double averageStrength(Game game, List<Country> countries){
        double total = 0;
        for(Country country : countries){
            total += game.getOccupationForce(country);
        }
        return total / game.getAllCountries().size();
    }

    public static boolean hasMajorityArmies(Player player, Game game){
        int myArmyCount = AiUtils.findAllPlayerArmies(game, player);
        int enemyArmyCount = AiUtils.findAllOppositionArmies(game, player);
        return myArmyCount > enemyArmyCount;
    }

    public static boolean allCountriesContiguous(Player player, Game game){
        List<Country> possessions = game.findOccupiedCountries(player);
        return MapUtils.isContiguousBloc(game.getMap(), possessions);
    }

}
