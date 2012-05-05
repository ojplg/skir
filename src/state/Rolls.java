package state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rolls {

    private List<Integer> _attackRolls;
    private List<Integer> _defenseRolls;
    private int _attackersLosses = 0;
    private int _defendersLosses = 0;

    public Rolls(int[] attacks, int[] defense){
        _attackRolls = reverseSort(attacks);
        _defenseRolls = reverseSort(defense);
        resolve();
    }

    public int defendersLosses(){
        return _defendersLosses;
    }

    public int attackersLosses(){
        return _attackersLosses;
    }

    private void resolve(){
        int battles = Math.min(_attackRolls.size(), _defenseRolls.size());
        for(int idx=0; idx<battles; idx++ ){
            if( attackerWins(_attackRolls.get(idx), _defenseRolls.get(idx))) {
                _defendersLosses++;
            } else {
                _attackersLosses++;
            }
        }
    }

    public boolean attackerWins(Integer attackRoll, Integer defenseRoll){
        return attackRoll > defenseRoll;
    }

    private List<Integer> reverseSort(int[] nums){
        List<Integer> list = new ArrayList<Integer>();
        for (int n : nums){
            list.add(new Integer(n));
        }
        Collections.sort(list);
        Collections.reverse(list);
        return list;
    }
}
