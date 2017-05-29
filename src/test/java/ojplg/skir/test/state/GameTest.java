package ojplg.skir.test.state;

import ojplg.skir.state.Game;
import ojplg.skir.test.helper.GameHelper;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class GameTest {

    @Test
    public void testHasLegalFortification(){
        GameHelper helper = new GameHelper();
        Game game = helper.Game;
        assertFalse(game.hasLegalFortification(helper.RedPlayer));
    }

    @Test
    public void testHasLegalAttack_No(){
        GameHelper helper = new GameHelper();
        Game game = helper.Game;
        assertFalse(game.hasPossibleAttack(helper.RedPlayer));
    }

    @Test
    public void testHasLegalAttack_Yes(){
        GameHelper helper = new GameHelper();
        helper.setUpPlayerForAttack(helper.RedPlayer);
        Game game = helper.Game;
        assertTrue(game.hasPossibleAttack(helper.RedPlayer));
    }

}
