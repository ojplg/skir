package ojplg.skir.play;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Rolls {

    private static final double ONE_V_ONE_ATTACKER_EXPECTATIONS = 0.41666666;
    private static final double TWO_V_ONE_ATTACKER_EXPECTATIONS = 0.5787037;
    private static final double THREE_V_ONE_ATTACKER_EXPECTATIONS = 0.6597222;
    private static final double ONE_V_TWO_ATTACKER_EXPECTATIONS = 0.25462964;
    private static final double TWO_V_TWO_ATTACKER_EXPECTATIONS = 0.3896605;
    private static final double THREE_V_TWO_ATTACKER_EXPECTATIONS = 0.53954476;

    private final List<Integer> _attackRolls;
    private final List<Integer> _defenseRolls;
    private int _attackersLosses = 0;
    private int _defendersLosses = 0;

    public Rolls(int[] attacks, int[] defense){
        _attackRolls = Collections.unmodifiableList(reverseSort(attacks));
        _defenseRolls = Collections.unmodifiableList(reverseSort(defense));
        resolve();
    }

    public int defendersLosses(){
        return _defendersLosses;
    }

    public int attackersLosses(){
        return _attackersLosses;
    }

    private void resolve(){
        int battles = numberBattles();
        for(int idx=0; idx<battles; idx++ ){
            if( attackerWins(_attackRolls.get(idx), _defenseRolls.get(idx))) {
                _defendersLosses++;
            } else {
                _attackersLosses++;
            }
        }
    }

    private boolean attackerWins(Integer attackRoll, Integer defenseRoll){
        return attackRoll > defenseRoll;
    }

    public double attackersExpectationsDifference(){
        return _defendersLosses - numberBattles() * attackersExpectations();
    }

    public double defendersExpectationsDifference(){
        return 0 - attackersExpectationsDifference();
    }

    private double attackersExpectations(){
        int attackDice = _attackRolls.size();
        int defenseDice = _defenseRolls.size();
        if( defenseDice == 1){
            if (attackDice == 1){
                return ONE_V_ONE_ATTACKER_EXPECTATIONS;
            } else if (attackDice == 2){
                return TWO_V_ONE_ATTACKER_EXPECTATIONS;
            } else if (attackDice == 3){
                return THREE_V_ONE_ATTACKER_EXPECTATIONS;
            }
        } else if (defenseDice == 2){
            if (attackDice == 1){
                return ONE_V_TWO_ATTACKER_EXPECTATIONS;
            } else if (attackDice == 2){
                return TWO_V_TWO_ATTACKER_EXPECTATIONS;
            } else if (attackDice == 3){
                return THREE_V_TWO_ATTACKER_EXPECTATIONS;
            }
        }
        throw new RuntimeException("Do not know odds for " + attackDice + " v " + defenseDice);
    }

    public int numberBattles(){
        return Math.min(_attackRolls.size(), _defenseRolls.size());
    }

    private List<Integer> reverseSort(int[] nums){
        List<Integer> list = new ArrayList<>();
        for (int n : nums){
            list.add(n);
        }
        Collections.sort(list);
        Collections.reverse(list);
        return list;
    }
}
