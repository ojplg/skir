package play.orders;

import card.Card;
import card.CardStack;
import state.Game;
import state.Player;

public class ExchangeCardSet extends Order {

    Card _one;
    Card _two;
    Card _three;

    public ExchangeCardSet(Player player, Card one, Card two, Card three){
        super(player);
        _one = one;
        _two = two;
        _three = three;
    }

    public TurnPhase execute(Game game){

        if( ! CardStack.canTrade(_one, _two, _three) ) {
            throw new RuntimeException("Not a good set: " + _one + "," + _two + "," + _three);
        }

        int armies = game.tradeCards(_one, _two, _three);
        activePlayer().grantReserves(armies);

        return TurnPhase.Supply;
    }

}
