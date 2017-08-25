package ojplg.skir.ai;

import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

public class MassyTwo implements AutomatedPlayer {

    private static final String NAME = "MassyTwo";
    private final Player _me;

    public MassyTwo(Player player){
        _me = player;
        _me.setDisplayName(NAME);
    }

    @Override
    public void initialize(Game game) {
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {
        if(AiUtils.hasMajorityArmies(_me, game)){
            Bully bully = new Bully(_me, NAME);
            return bully.generateOrder(adjutant, game);
        } else {
            Massy massy = new Massy(_me, NAME);
            return massy.generateOrder(adjutant, game);
        }
    }

    @Override
    public Player getPlayer() {
        return _me;
    }

}
