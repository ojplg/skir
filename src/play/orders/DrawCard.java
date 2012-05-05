package play.orders;

import state.Game;
import state.Player;

public class DrawCard extends Order {

    public DrawCard(Player player){
        super(player);
    }

    @Override
    TurnPhase execute(Game game) {
        // TODO: have to find if the player is entitled to draw a card
        activePlayer().addCard(game.drawCard());
        return TurnPhase.Trade;
    }
}
