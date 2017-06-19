package ojplg.skir.play.orders;

import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.state.PlayerHoldings;

public class DrawCard extends Order {

    public DrawCard(Adjutant adjutant){
        super(adjutant);
    }

    @Override
    public Adjutant execute(Game game) {
        PlayerHoldings activeHoldings = game.getPlayerHoldings(activePlayer());

        if( getAdjutant().hasConqueredCountry() && ! activeHoldings.hasMaximumCards() ){
            activeHoldings.addCard(game.processDrawCardOrder());
            game.publishPlayerState(activePlayer());
        }
        Player nextPlayer = game.nextPlayer();
        return getAdjutant().nextPlayer(nextPlayer, game.getTurnNumber());
    }

    @Override
    public OrderType getType() {
        return OrderType.DrawCard;
    }
}
