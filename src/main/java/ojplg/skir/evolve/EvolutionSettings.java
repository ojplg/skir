package ojplg.skir.evolve;

import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.ai.TuneyTwo;
import ojplg.skir.state.Player;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class EvolutionSettings {

    public static final int GAMES_PER_INDIVIDUAL = 100;
    public static final int NUMBER_OF_GENERATIONS = 250;
    public static final int GENERATION_SIZE = 64;

    private final int _gamesPerIndividual;
    private final int _numberGenerations;
    private final int _generationSize;
    private Set<String> _settingNames;
    private BiFunction<Player, Map<String,Double>, AutomatedPlayer> _playerGenerator;

    public EvolutionSettings(int gamesPerIndividual, int numberGenerations, int generationSize){
        this._gamesPerIndividual = gamesPerIndividual;
        this._numberGenerations = numberGenerations;
        this._generationSize = generationSize;

        this._settingNames = TuneyTwo.presetTunings().keySet();
        this._playerGenerator = TuneyTwo::new;
    }

    public int getGamesPerIndividual() {
        return _gamesPerIndividual;
    }

    public int getNumberGenerations() {
        return _numberGenerations;
    }

    public int getGenerationSize() {
        return _generationSize;
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
