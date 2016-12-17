package ojplg.skir.ai;

import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.state.Constants;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AiUtils {

    public static Continent findStrongestUnownedContinent(Player player, Game game){
        Map<Continent, Float> countryPercentages = computeCountryPercentages(player, game);
        Map<Continent, Float> armyPercentages = computeArmyPercentages(player, game);

        Continent best = null;
        float highest = 0;
        for(Continent continent : game.getAllContinents()){
            Float total = countryPercentages.get(continent) + armyPercentages.get(continent);
            if( total > 1.99999 ){
                continue;
            }
            if( total > highest ){
                best = continent;
                highest = total;
            }
        }
        return best;
    }

    public static Map<Continent, Float> computeCountryPercentages(Player player, Game game){
        Map<Continent, Float> ownedPercentages = new HashMap<>();
        for(Continent continent : game.getAllContinents()){
            Float percentage = computeCountryPercentage(player, continent, game);
            ownedPercentages.put(continent, percentage);
        }
        return ownedPercentages;
    }

    public static Map<Continent, Float> computeArmyPercentages(Player player, Game game){
        Map<Continent, Float> ownedPercentages = new HashMap<>();
        for(Continent continent : game.getAllContinents()){
            Float percentage = computeArmyPercentage(player, continent, game);
            ownedPercentages.put(continent, percentage);
        }
        return ownedPercentages;
    }

    public static Float computeCountryPercentage(Player player, Continent continent, Game game){
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

    public static Float computeArmyPercentage(Player player, Continent continent, Game game){
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
    public static List<PossibleAttack> findAdvantageousAttacks(Player player, Game game){
        List<PossibleAttack> advantages = new ArrayList<>();
        for(Country country : game.countriesToAttackFrom(player)){
            int myForce = game.getOccupationForce(country);
            for(Country enemyNeighbor : game.enemyNeighbors(country)){
                int enemyForce = game.getOccupationForce(enemyNeighbor);
                if( myForce > enemyForce){
                    advantages.add(new PossibleAttack(country, enemyNeighbor, enemyForce - myForce));
                }
            }
        }
        return advantages;
    }

    public static Country findWeakestPossession(Player player, Game game){
        List<Country> possessions = game.countriesOccupied(player);
        return possessions.stream()
                .min((c1, c2) -> (game.getOccupationForce(c1) - game.getOccupationForce(c2)))
                .get();
    }

    public static int attackingDice(Game game, Country country){
        return Math.min(Constants.MAXIMUM_ATTACKER_DICE,
                game.getOccupationForce(country) - 1);
    }

}
