package web;

import org.json.simple.JSONObject;

public interface ClientMessageReceiver {
    void onMessage(JSONObject jObject);
}
