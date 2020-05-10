package ojplg.skir.play.orders;

import org.json.simple.JSONObject;

public interface OrderConstraints {

    boolean allowableOrder(Order order);

    JSONObject toJsonObject();
}
