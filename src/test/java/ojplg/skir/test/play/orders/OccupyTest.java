package ojplg.skir.test.play.orders;

import ojplg.skir.map.Country;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Occupy;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.state.PlayerHoldings;
import ojplg.skir.test.helper.GameHelper;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class OccupyTest {

    @Test
    public void testCardStealingRequiresExchange(){
        GameHelper gameHelper = new GameHelper();
        Player victor = gameHelper.RedPlayer;
        Player loser = gameHelper.WhitePlayer;
        gameHelper.setUpPreEliminationCondition(victor, loser, Country.Afghanistan, Country.Middle_East);
        Game game = gameHelper.Game;
        PlayerHoldings victorHoldings = game.getPlayerHoldings(victor);

        victorHoldings.grantReserves(5);
        victorHoldings.addCard(game.processDrawCardOrder());
        victorHoldings.addCard(game.processDrawCardOrder());
        victorHoldings.addCard(game.processDrawCardOrder());
        victorHoldings.addCard(game.processDrawCardOrder());

        PlayerHoldings loserHoldings = game.getPlayerHoldings(victor);

        loserHoldings.addCard(game.processDrawCardOrder());
        loserHoldings.addCard(game.processDrawCardOrder());
        loserHoldings.addCard(game.processDrawCardOrder());
        loserHoldings.addCard(game.processDrawCardOrder());

        Adjutant adjutant = new Adjutant(victor, true, OrderType.Attack, 1);

        Occupy occupy = new Occupy(adjutant, Country.Afghanistan, Country.Middle_East, 1);
        Adjutant postOccupyAdjutant = occupy.execute(game);
        List<OrderType> allowableOrders = postOccupyAdjutant.allowableOrders();
        assertEquals(1,allowableOrders.size());
        assertEquals(OrderType.ExchangeCardSet, allowableOrders.get(0));
    }

}
