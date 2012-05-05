package play;

import state.Rolls;

public interface Roller {
    Rolls roll(int numberAttackers, int numberDefenders);
}
