package ojplg.skir.evolve;

import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.ai.TuneyTwo;
import ojplg.skir.state.Player;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class EvolutionSettings {

    private static final int GAMES_PER_INDIVIDUAL = 100;
    private static final int NUMBER_OF_GENERATIONS = 250;
    private static final int GENERATION_SIZE = 64;

    public EvolutionSettings(){
        this._gamesPerIndividual = GAMES_PER_INDIVIDUAL;
        this._numberGenerations = NUMBER_OF_GENERATIONS;
        this._generationSize = GENERATION_SIZE;

        this._settingNames = TuneyTwo.presetTunings().keySet();
        this._playerGenerator = (p, t) -> new TuneyTwo(p, t);
    }

    private int _gamesPerIndividual;
    private int _numberGenerations;
    private int _generationSize;
    private Set<String> _settingNames;
    private BiFunction<Player, Map<String,Double>, AutomatedPlayer> _playerGenerator;

    public int getGamesPerIndividual() {
        return _gamesPerIndividual;
    }

    public void setGamesPerIndividual(int gamesPerIndividual) {
        this._gamesPerIndividual = gamesPerIndividual;
    }

    public int getNumberGenerations() {
        return _numberGenerations;
    }

    public void setNumberGenerations(int numberGenerations) {
        this._numberGenerations = numberGenerations;
    }

    public int getGenerationSize() {
        return _generationSize;
    }

    public void setGenerationSize(int generationSize) {
        this._generationSize = generationSize;
    }

    public Set<String> getSettingNames() {
        return _settingNames;
    }

    public BiFunction<Player, Map<String, Double>, AutomatedPlayer> getPlayerGenerator() {
        return _playerGenerator;
    }

    @Override
    public String toString() {
        return "EvolutionSettings{" +
                "_gamesPerIndividual=" + _gamesPerIndividual +
                ", _numberGenerations=" + _numberGenerations +
                ", _generationSize=" + _generationSize +
                '}';
    }
}
