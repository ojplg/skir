package ojplg.skir.test.ai;

import ojplg.skir.ai.Tuney;
import ojplg.skir.map.Country;
import ojplg.skir.test.helper.GameHelper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TuneyTest {

    @Test
    public void testEnemyContinentBorderIncreasesPlacementChances(){
        GameHelper gameHelper = new GameHelper();

        gameHelper.setCountry(Country.Argentina, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Brazil, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Peru, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Venezuela, gameHelper.RedPlayer, 1);

        gameHelper.setCountry(Country.Central_America, gameHelper.BlackPlayer, 1);

        Tuney tuney = new Tuney(gameHelper.BlackPlayer, Tuney.presetTunings(),"TuneyUnitTest");

        double continentSplitValue = tuney.computePlacementScore(Country.Central_America, gameHelper.Game);

        gameHelper.setCountry(Country.Venezuela, gameHelper.BluePlayer, 1);

        double continentOwnedValue = tuney.computePlacementScore(Country.Central_America, gameHelper.Game);

        assertTrue( continentSplitValue < continentOwnedValue);

    }
}
