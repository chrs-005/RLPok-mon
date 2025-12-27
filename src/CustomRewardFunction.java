package src.pas.pokemon.rewards;


// SYSTEM IMPORTS


// JAVA PROJECT IMPORTS
import edu.bu.pas.pokemon.agents.rewards.RewardFunction;
import edu.bu.pas.pokemon.agents.rewards.RewardFunction.RewardType;
import edu.bu.pas.pokemon.core.Battle.BattleView;
import edu.bu.pas.pokemon.core.Move.MoveView;
import edu.bu.pas.pokemon.core.Pokemon.PokemonView;
import edu.bu.pas.pokemon.core.Team.TeamView;
import edu.bu.pas.pokemon.core.enums.Stat;

public class CustomRewardFunction
    extends RewardFunction
{

    public CustomRewardFunction()
    {
        super(RewardType.STATE_ACTION); // currently configured to produce rewards as a function of the state
    }

    public double getLowerBound()
    {
        // TODO: change this. Reward values must be finite!
        return 30;
    }

    public double getUpperBound()
    {
        // TODO: change this. Reward values must be finite!
        return -30;
    }

    public double getStateReward(final BattleView state)
    {
        return 0d;
    }

    public double getStateActionReward(final BattleView state,
                                       final MoveView action)
    {
        TeamView myTeam  = state.getTeam1View();
        TeamView oppTeam = state.getTeam2View();

        double myCurHp  = 0.0;
        double myMaxHp  = 0.0;
        double oppCurHp = 0.0;
        double oppMaxHp = 0.0;

        for (int i = 0; i < myTeam.size(); ++i) {
            PokemonView p = myTeam.getPokemonView(i);
            myCurHp += p.getCurrentStat(Stat.HP);
            myMaxHp += p.getInitialStat(Stat.HP);
        }

        for (int i = 0; i < oppTeam.size(); ++i) {
            PokemonView p = oppTeam.getPokemonView(i);
            oppCurHp += p.getCurrentStat(Stat.HP);
            oppMaxHp += p.getInitialStat(Stat.HP);
        }

        double myFrac  = (myMaxHp  > 0.0) ? (myCurHp  / myMaxHp)  : 0.0;
        double oppFrac = (oppMaxHp > 0.0) ? (oppCurHp / oppMaxHp) : 0.0;


        double reward = 10.0 * (myFrac - oppFrac);

        boolean iLost  = (myCurHp  <= 0.0) && (oppCurHp > 0.0);
        boolean iWon   = (oppCurHp <= 0.0) && (myCurHp  > 0.0);

        if (iWon) {
            reward += 20.0;
        } else if (iLost) {
            reward -= 20.0;
        }

        return reward;
    }

    public double getStateActionStateReward(final BattleView state,
                                            final MoveView action,
                                            final BattleView nextState)
    {
        return 0d;
    }

}
