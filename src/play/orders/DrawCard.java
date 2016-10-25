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
        return new Adjutant(game.nextPlayer(), game.getRoller());
    }

    @Override
    OrderType getType() {
        return OrderType.DrawCard;
    }
}
