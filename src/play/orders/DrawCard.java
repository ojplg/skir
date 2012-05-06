package play.orders;

import state.Game;
import state.Player;

import java.util.Collections;

public class DrawCard extends Order {

    public DrawCard(Adjutant adjutant){
        super(adjutant);
    }

    @Override
    Adjutant execute(Game game) {
        if( getAdjutant().hasConqueredCountry() ){
            activePlayer().addCard(game.drawCard());
        }
        getAdjutant().setAllowableOrders();
        return getAdjutant();
    }

    @Override
    OrderType getType() {
        return OrderType.DrawCard;
    }
}
