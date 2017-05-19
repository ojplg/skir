package ojplg.skir.evolve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Generation {

    private final List<Individual> _members;

    public Generation(List<Individual> individuals){
        _members = Collections.unmodifiableList(individuals);
    }

    public boolean hasUnscoredIndividual(){
        return _members.stream().anyMatch(i -> i.isUnscored());
    }

    public Individual getUnscoredIndividual(){
        Optional<Individual> maybeIndividual = _members.stream()
                .filter(i -> i.isUnscored()).findFirst();
        if (maybeIndividual.isPresent()){
            return maybeIndividual.get();
        }
        return null;
    }

    public int getCount(){
        return _members.size();
    }

    public List<Individual> findTopTenPercent(){
        List<Individual> members = new ArrayList<>(_members);
        Collections.sort(members);
        Collections.reverse(members);
        int cnt = members.size() / 10;
        return members.subList(0, cnt);
    }
}
