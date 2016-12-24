package ojplg.skir.ai;

import ojplg.skir.map.Country;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

public class Tuner implements AutomatedPlayer {

    private static final String BorderCountryPlacementKey = "BorderCountryPlacementKey";

    private final Player _me;



    public Tuner(Player player){
        player.setDisplayName("Tuner");
        _me = player;
    }

    @Override
    public void initialize(Game game) {
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {
        return null;
    }

    @Override
    public Player getPlayer() {
        return _me;
    }

    @Override
    public Object getIdentification() {
        return "Tuner";
    }

    private float computePlacementScore(Country country, Game game){
        float score = 1;

        boolean isBorderCountry = AiUtils.isBorderCountry(_me, game, country);

        return score;
    }
}
