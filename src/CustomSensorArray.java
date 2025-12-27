package src.pas.pokemon.senses;


// SYSTEM IMPORTS


// JAVA PROJECT IMPORTS
import edu.bu.pas.pokemon.agents.senses.SensorArray;
import edu.bu.pas.pokemon.core.Battle.BattleView;
import edu.bu.pas.pokemon.core.Move.MoveView;
import edu.bu.pas.pokemon.core.Pokemon.PokemonView;
import edu.bu.pas.pokemon.core.Team.TeamView;
import edu.bu.pas.pokemon.linalg.Matrix;
import edu.bu.pas.pokemon.core.enums.NonVolatileStatus;
import edu.bu.pas.pokemon.core.enums.Stat;



public class CustomSensorArray
    extends SensorArray
{

    // TODO: make fields if you want!

    public CustomSensorArray()
    {
        // TODO: intialize those fields if you make any!
        super();
    }

    public Matrix getSensorValues(final BattleView state, final MoveView action)
    {
        // TODO: Convert a BattleView and a MoveView into a row-vector containing measurements for every sense
        // you want your neural network to have. This method should be called if your model is a q-based model

        Matrix features = Matrix.zeros(1, 39);
        int idx = 0;


        TeamView team1 = state.getTeam1View();
        TeamView team2 = state.getTeam2View();

        PokemonView myActive  =  team1.getActivePokemonView() ;
        PokemonView oppActive =  team2.getActivePokemonView() ;

        // ---------- Active Pokémon stats: my side ----------
        // HP fraction, then ATK, DEF, SPATK, SPDEF, SPD
        double myCurrentHp  = myActive.getCurrentStat(Stat.HP);
        double myInitialHp  = myActive.getInitialStat(Stat.HP);
        double myHpFrac     = (myInitialHp > 0.0) ? (myCurrentHp / myInitialHp) : 0.0;

        features.set(0, idx++, myHpFrac);
        features.set(0, idx++, myActive.getCurrentStat(Stat.ATK));
        features.set(0, idx++, myActive.getCurrentStat(Stat.DEF));
        features.set(0, idx++, myActive.getCurrentStat(Stat.SPATK));
        features.set(0, idx++, myActive.getCurrentStat(Stat.SPDEF));
        features.set(0, idx++, myActive.getCurrentStat(Stat.SPD));

        // ---------- Active Pokémon stats: opponent ----------
        double oppCurrentHp = oppActive.getCurrentStat(Stat.HP);
        double oppInitialHp = oppActive.getInitialStat(Stat.HP);
        double oppHpFrac    = (oppInitialHp > 0.0) ? (oppCurrentHp / oppInitialHp) : 0.0;

        features.set(0, idx++, oppHpFrac);
        features.set(0, idx++, oppActive.getCurrentStat(Stat.ATK));
        features.set(0, idx++, oppActive.getCurrentStat(Stat.DEF));
        features.set(0, idx++, oppActive.getCurrentStat(Stat.SPATK));
        features.set(0, idx++, oppActive.getCurrentStat(Stat.SPDEF));
        features.set(0, idx++, oppActive.getCurrentStat(Stat.SPD));

        // ---------- Status bits: my side ----------
        NonVolatileStatus myStatus = myActive.getNonVolatileStatus();
        features.set(0, idx++, (myStatus == NonVolatileStatus.SLEEP)     ? 1.0 : 0.0);
        features.set(0, idx++, (myStatus == NonVolatileStatus.PARALYSIS) ? 1.0 : 0.0);
        features.set(0, idx++, (myStatus == NonVolatileStatus.POISON)    ? 1.0 : 0.0);
        features.set(0, idx++, (myStatus == NonVolatileStatus.TOXIC)     ? 1.0 : 0.0);
        features.set(0, idx++, (myStatus == NonVolatileStatus.BURN)      ? 1.0 : 0.0);
        features.set(0, idx++, (myStatus == NonVolatileStatus.FREEZE)    ? 1.0 : 0.0);

        // ---------- Stat multipliers: my side ----------
        features.set(0, idx++, myActive.getStatMultiplier(Stat.ATK));
        features.set(0, idx++, myActive.getStatMultiplier(Stat.DEF));
        features.set(0, idx++, myActive.getStatMultiplier(Stat.SPATK));
        features.set(0, idx++, myActive.getStatMultiplier(Stat.SPDEF));
        features.set(0, idx++, myActive.getStatMultiplier(Stat.SPD));


        // ---------- Status bits: opponent side ----------
        NonVolatileStatus oppStatus = oppActive.getNonVolatileStatus();
        features.set(0, idx++, (oppStatus == NonVolatileStatus.SLEEP)     ? 1.0 : 0.0);
        features.set(0, idx++, (oppStatus == NonVolatileStatus.PARALYSIS) ? 1.0 : 0.0);
        features.set(0, idx++, (oppStatus == NonVolatileStatus.POISON)    ? 1.0 : 0.0);
        features.set(0, idx++, (oppStatus == NonVolatileStatus.TOXIC)     ? 1.0 : 0.0);
        features.set(0, idx++, (oppStatus == NonVolatileStatus.BURN)      ? 1.0 : 0.0);
        features.set(0, idx++, (oppStatus == NonVolatileStatus.FREEZE)    ? 1.0 : 0.0);

        // ---------- Stat multipliers: opponent side ----------
        features.set(0, idx++, oppActive.getStatMultiplier(Stat.ATK));
        features.set(0, idx++, oppActive.getStatMultiplier(Stat.DEF));
        features.set(0, idx++, oppActive.getStatMultiplier(Stat.SPATK));
        features.set(0, idx++, oppActive.getStatMultiplier(Stat.SPDEF));
        features.set(0, idx++, oppActive.getStatMultiplier(Stat.SPD));


        // ---------- Team-level info: number of alive Pokémon ----------
        // team1 alive count
        int alive1 = 0;
        for (int i = 0; i < team1.size(); ++i) {
            PokemonView p = team1.getPokemonView(i);
            if (p != null && !p.hasFainted()) {
                ++alive1;
            }
        }
        features.set(0, idx++, alive1);

        // team2 alive count
        int alive2 = 0;
        for (int i = 0; i < team2.size(); ++i) {
            PokemonView p = team2.getPokemonView(i);
            if (p != null && !p.hasFainted()) {
                ++alive2;
            }
        }
        features.set(0, idx++, alive2);

        // ---------- Move features for the given action ----------
        // raw power
        features.set(0, idx++, action.getPower());

        // base accuracy (usually 0-100)
        features.set(0, idx++, action.getAccuracy());

        // priority (integer, e.g. -6..+6)
        features.set(0, idx++, action.getPriority());


        return features;

    }

}
