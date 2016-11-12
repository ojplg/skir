package play.orders;

import org.json.simple.JSONObject;

public class UnconstrainedOrder implements OrderConstraints {
    @Override
    public JSONObject toJsonObject() {
        return new JSONObject();
    }
}
