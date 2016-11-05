package play.orders;

import play.RandomRoller;
import state.Game;
import state.Player;

import java.util.Collections;

public class DrawCard extends Order {

    public DrawCard(Adjutant adjutant){
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        if( getAdjutant().hasConqueredCountry() ){
            activePlayer().addCard(game.drawCard());
        }
        Player nextPlayer = game.nextPlayer();
        return new Adjutant(nextPlayer);
    }

    @Override
    OrderType getType() {
        return OrderType.DrawCard;
    }
}
