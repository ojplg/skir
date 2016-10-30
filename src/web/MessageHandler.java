package web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import play.GameRunner;

public class MessageHandler implements ClientMessageReceiver {

    private static final Logger _log = LogManager.getLogger(MessageHandler.class);

    private final GameRunner _gameRunner;

    public MessageHandler(GameRunner gameRunner){
        _gameRunner = gameRunner;
    }

    /*
    {"defense-country":"Western Australia",
    "message-type":"attack-command",
    "attack-country":"Eastern Australia"}
    */

    @Override
    public void onMessage(JSONObject jObject) {
        String messageType = (String) jObject.get("message-type");
        if( messageType.equals("attack-command")) {
            handleAttackCommand(jObject);
        }
    }

    private void handleAttackCommand(JSONObject jObject){
        String attackingCountry = (String) jObject.get("attack-country");
        String defendingCountry = (String) jObject.get("defense-country");
        String attackType = (String) jObject.get("all-out-flag");
        String color = (String) jObject.get("color");
        if( attackType.equals("once")) {
            _gameRunner.doAttack(color, attackingCountry, defendingCountry);
        } else if ( attackType.equals("all-out")){
            _gameRunner.doAllOutAttack(color, attackingCountry, defendingCountry);
        }
    }
}
