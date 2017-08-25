package ojplg.skir.test.ai;

import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.ai.AiUtils;
import ojplg.skir.test.helper.GameHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class AiUtilsTest {

    @Test
    public void testEnemyOwnedContinents(){
        GameHelper gameHelper = new GameHelper();

        gameHelper.setCountry(Country.Argentina, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Brazil, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Peru, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Venezuela, gameHelper.RedPlayer, 1);

        List<Continent> enemyContinents = AiUtils.enemyOwnedContinents(gameHelper.BlackPlayer, gameHelper.Game);
        assertTrue( enemyContinents.size() == 0);

        gameHelper.setCountry(Country.Venezuela, gameHelper.BluePlayer, 1);

        enemyContinents = AiUtils.enemyOwnedContinents(gameHelper.BlackPlayer, gameHelper.Game);
        assertTrue( enemyContinents.size() == 1);
        assertTrue(enemyContinents.contains(Continent.South_America));

    }

    @Test
    public void testFindStrongestUnownedContinent(){
        GameHelper gameHelper = new GameHelper();

        gameHelper.setCountry(Country.Argentina, gameHelper.BluePlayer, 3);
        gameHelper.setCountry(Country.Brazil, gameHelper.BluePlayer, 4);
        gameHelper.setCountry(Country.Peru, gameHelper.BluePlayer, 5);
        gameHelper.setCountry(Country.Venezuela, gameHelper.RedPlayer, 2);

        Continent continent = AiUtils.findStrongestUnownedContinent(gameHelper.BluePlayer, gameHelper.Game);

        assertEquals(Continent.South_America, continent);
    }

    @Test
    public void testFindStrongestUnownedContinent_RejectsOwnedContinent(){
        GameHelper gameHelper = new GameHelper();

        gameHelper.setCountry(Country.Argentina, gameHelper.BluePlayer, 3);
        gameHelper.setCountry(Country.Brazil, gameHelper.BluePlayer, 4);
        gameHelper.setCountry(Country.Peru, gameHelper.BluePlayer, 5);
        gameHelper.setCountry(Country.Venezuela, gameHelper.BluePlayer, 2);

        Continent continent = AiUtils.findStrongestUnownedContinent(gameHelper.BluePlayer, gameHelper.Game);

        assertNotEquals(Continent.South_America, continent);
    }

    @Test
    public void testContinentalArmyPercentage(){
        GameHelper gameHelper = new GameHelper();

        gameHelper.setCountry(Country.Argentina, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Brazil, gameHelper.WhitePlayer, 4);
        gameHelper.setCountry(Country.Peru, gameHelper.BluePlayer, 5);
        gameHelper.setCountry(Country.Venezuela, gameHelper.BlackPlayer, 2);

        double armyPercentage = AiUtils.continentalArmyPercentage(gameHelper.BluePlayer, gameHelper.Game,
                Continent.South_America);

        assertEquals(0.5, armyPercentage, 0.00001);
    }

    @Test
    public void testFindContiguousOwnedCountries(){
        GameHelper gameHelper = new GameHelper();

        for(Country country : Continent.Asia.getCountries()){
            gameHelper.setCountry(country, gameHelper.RedPlayer,1);
        }
        gameHelper.setCountry(Country.China, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.India, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Mongolia, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Siberia, gameHelper.BluePlayer, 1);

        Set<Country> bloc = AiUtils.findContiguousOwnedCountries(gameHelper.Game, Country.China);

        assertEquals(4, bloc.size());
        assertTrue(bloc.contains(Country.China));
        assertTrue(bloc.contains(Country.India));
        assertTrue(bloc.contains(Country.Mongolia));
        assertTrue(bloc.contains(Country.Siberia));
    }

    @Test
    public void testFindContiguousOwnedCountriesSpansContinents(){
        GameHelper gameHelper = new GameHelper();

        for(Country country : Continent.Asia.getCountries()){
            gameHelper.setCountry(country, gameHelper.RedPlayer,1);
        }
        for(Country country : Continent.Australia.getCountries()){
            gameHelper.setCountry(country, gameHelper.GreenPlayer,1);
        }
        gameHelper.setCountry(Country.China, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.India, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Mongolia, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Siberia, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Siam, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Indonesia, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.New_Guinea, gameHelper.BluePlayer, 1);

        Set<Country> bloc = AiUtils.findContiguousOwnedCountries(gameHelper.Game, Country.China);

        assertEquals(7, bloc.size());
        assertTrue(bloc.contains(Country.China));
        assertTrue(bloc.contains(Country.India));
        assertTrue(bloc.contains(Country.Mongolia));
        assertTrue(bloc.contains(Country.Siberia));
        assertTrue(bloc.contains(Country.Siam));
        assertTrue(bloc.contains(Country.Indonesia));
        assertTrue(bloc.contains(Country.New_Guinea));
    }

    @Test
    public void testFindEnemyBorders(){
        GameHelper gameHelper = new GameHelper();
        gameHelper.setAllCountries(gameHelper.WhitePlayer, 1);
        gameHelper.setCountry(Country.Central_America, gameHelper.RedPlayer, 3);
        gameHelper.setCountry(Country.China, gameHelper.RedPlayer, 1);

        Set<Country> enemyBorders = AiUtils.findEnemyBorders(gameHelper.RedPlayer, gameHelper.Game);
        Assert.assertEquals(9, enemyBorders.size());
        Assert.assertTrue(enemyBorders.contains(Country.India));
        Assert.assertTrue(enemyBorders.contains(Country.Venezuela));
    }

}
