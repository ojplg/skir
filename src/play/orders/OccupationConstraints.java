package play.orders;

import map.Country;
import org.json.simple.JSONObject;

public class OccupationConstraints implements OrderConstraints {

    private final Attack _conquest;
    private final int _maximumOccupationForce;

    public OccupationConstraints(Attack attack, int maximumAvailableForOccupation){
        this._conquest = attack;
        this._maximumOccupationForce = maximumAvailableForOccupation;
    }

    public int minimumOccupationForce(){
        return _conquest.getAttackersDiceCount();
    }

    public int maximumOccupationForce() { return _maximumOccupationForce; }

    public Country attacker(){
        return _conquest.getInvader();
    }

    public Country conquered(){
        return _conquest.getTarget();
    }

    @Override
    public JSONObject toJsonObject(){
        JSONObject jObject = new JSONObject();
        jObject.put("minimum_occupation_force", minimumOccupationForce());
        jObject.put("maximum_occupation_force", maximumOccupationForce());
        jObject.put("attacker", attacker().getName());
        jObject.put("conquered", conquered().getName());
        return jObject;
    }

    @Override
    public String toString() {
        return "OccupationConstraints{" +
                "_conquest=" + _conquest +
                ", _maximumOccupationForce=" + _maximumOccupationForce +
                '}';
    }
}
