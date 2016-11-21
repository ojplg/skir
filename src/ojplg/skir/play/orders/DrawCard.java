package ojplg.skir.play.orders;

import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

public class DrawCard extends Order {

    public DrawCard(Adjutant adjutant){
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        if( getAdjutant().hasConqueredCountry() && ! activePlayer().hasMaximumCards() ){
            activePlayer().addCard(game.drawCard());
            game.publishPlayerChanged(activePlayer());
        }
        Player nextPlayer = game.nextPlayer();
        return Adjutant.nextPlayer(nextPlayer);
    }

    @Override
    public OrderType getType() {
        return OrderType.DrawCard;
    }
}