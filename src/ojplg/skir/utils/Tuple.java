package ojplg.skir.utils;

public class Tuple<F,S> {
    private final F _first;
    private final S _second;

    public Tuple(F first, S second){
        _first = first;
        _second = second;
    }

    public F getFirst(){
        return _first;
    }

    public S getSecond(){
        return _second;
    }
}
