package play.orders;

import map.Country;

public class OccupationConstraints implements OrderConstraints {

    private final Attack _conquest;

    public OccupationConstraints(Attack attack){
        this._conquest = attack;
    }

    public int minimumOccupationForce(){
        return _conquest.getAttackersDiceCount();
    }

    public Country attacker(){
        return _conquest.getInvader();
    }

    public Country conquered(){
        return _conquest.getTarget();
    }

    @Override
    public String toString() {
        return "OccupationConstraints{" +
                "_conquest=" + _conquest +
                '}';
    }
}
