package ojplg.skir.test.ai;

import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.ai.AiUtils;
import ojplg.skir.test.helper.GameHelper;
import org.junit.Test;

import java.util.List;

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


}
