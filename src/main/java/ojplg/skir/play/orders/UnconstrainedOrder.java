package ojplg.skir.play.orders;

import org.json.simple.JSONObject;

public class UnconstrainedOrder implements OrderConstraints {
    @Override
    public JSONObject toJsonObject() {
        return new JSONObject();
    }

    @Override
    public boolean allowableOrder(Order order) {
        return true;
    }
}
