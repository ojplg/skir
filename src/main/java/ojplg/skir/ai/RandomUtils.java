package ojplg.skir.ai;

import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static final Random _random = new Random();

    public static <T> T pickRandomElement(List<T> ts){
        if( ts == null || ts.isEmpty()){
            return null;
        }
        int idx = _random.nextInt(ts.size());
        return ts.get(idx);
    }
}
