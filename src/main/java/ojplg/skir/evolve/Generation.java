package ojplg.skir.evolve;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Generation {

    private final static Logger _log = LogManager.getLogger("ojplg.skir.evolve");

    private final List<Individual> _members;
    private final int _number;

    public Generation(List<Individual> individuals, int number){
        _members = Collections.unmodifiableList(individuals);
        _number = number;
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

    public List<Individual> allMembers(){
        return _members;
    }

    public List<Individual> findTopIndividuals(){
        List<Individual> members = new ArrayList<>(_members);
        Collections.sort(members);
        Collections.reverse(members);
        _log.info("Average score of individuals in generation " + _number + " was " + averageScore(members));
        int cnt = (int) Math.sqrt(members.size());
        List<Individual> survivors = members.subList(0, cnt);
        _log.info("Average score of survivors in generation " + _number + " was " + averageScore(survivors));
        Individual best = survivors.get(0);
        JSONObject jObject = new JSONObject(best.getGenes());
        _log.info("Top survivor in generation " + _number + " was " + best.getIdentifier() + " with score " +
                best.getScore() + " with JSON" + jObject.toString());
        return survivors;
    }

    private Double averageScore(List<Individual> individuals){
        double sum = 0.0;
        for(Individual individual : individuals){
            sum += individual.getScore();
        }
        return sum/individuals.size();
    }

    public int getNumber(){
        return _number;
    }
}