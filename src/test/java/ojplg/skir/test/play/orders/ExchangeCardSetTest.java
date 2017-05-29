package ojplg.skir.test.play.orders;

import ojplg.skir.card.Card;
import ojplg.skir.card.CardSet;
import ojplg.skir.map.Country;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.ExchangeCardSet;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.test.helper.GameHelper;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExchangeCardSetTest {
    @Test
    public void testRequiresMultipleExchangesWhenPlayerHasTooManyCards(){
        GameHelper gameHelper = new GameHelper();
        Game game = gameHelper.Game;
        Player player = game.getOccupier(Country.Afghanistan);

        player.addCard(game.processDrawCardOrder());
        player.addCard(game.processDrawCardOrder());
        player.addCard(game.processDrawCardOrder());
        player.addCard(game.processDrawCardOrder());
        player.addCard(game.processDrawCardOrder());
        player.addCard(game.processDrawCardOrder());
        player.addCard(game.processDrawCardOrder());
        player.addCard(game.processDrawCardOrder());
        player.addCard(game.processDrawCardOrder());

        List<Card> cards = player.getCards();
        assertEquals(9, cards.size());

        CardSet set = CardSet.findTradeableSet(player.getCards());

        assertNotNull(set);

        Adjutant adjutant = new Adjutant(player, true, OrderType.ExchangeCardSet, 1);

        ExchangeCardSet exchangeCardSetOrder = new ExchangeCardSet(adjutant, set.getOne(), set.getTwo(), set.getThree());

        Adjutant newAdjutant = exchangeCardSetOrder.execute(game);
        List<OrderType> allowableOrders = newAdjutant.allowableOrders();
        assertEquals(1,allowableOrders.size());
        assertEquals(OrderType.ExchangeCardSet, allowableOrders.get(0));
    }

}
