package src.pas.pokemon.agents;


// SYSTEM IMPORTS
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.List;

import edu.bu.pas.pokemon.agents.NeuralQAgent;
import edu.bu.pas.pokemon.agents.senses.SensorArray;
import edu.bu.pas.pokemon.core.Battle.BattleView;
import edu.bu.pas.pokemon.core.Move.MoveView;
import edu.bu.pas.pokemon.linalg.Matrix;
import edu.bu.pas.pokemon.nn.Model;
import edu.bu.pas.pokemon.nn.models.Sequential;
import edu.bu.pas.pokemon.nn.layers.Dense; // fully connected layer
import edu.bu.pas.pokemon.nn.layers.ReLU;  // some activations (below too)
import edu.bu.pas.pokemon.nn.layers.Tanh;
import edu.bu.pas.pokemon.nn.layers.Sigmoid;
import edu.bu.pas.pokemon.core.Pokemon.PokemonView;
import edu.bu.pas.pokemon.core.enums.Stat;
// JAVA PROJECT IMPORTS
import src.pas.pokemon.senses.CustomSensorArray;

import java.util.List;
public class PolicyAgent
    extends NeuralQAgent
{
    private int moveCount = 0;


    public PolicyAgent()
    {
        super();
    }

    public void initializeSenses(Namespace args)
    {
        SensorArray modelSenses = new CustomSensorArray();

        this.setSensorArray(modelSenses);
    }

    @Override
    public void initialize(Namespace args)
    {
        // make sure you call this, this will call your initModel() and set a field
        // AND if the command line argument "inFile" is present will attempt to set
        // your model with the contents of that file.
        super.initialize(args);

        // what senses will your neural network have?
        this.initializeSenses(args);

        // do what you want just don't expect custom command line options to be available
        // when I'm testing your code
    }

    @Override
    public Model initModel()
    {
        // TODO: create your neural network

        // currently this creates a one-hidden-layer network
        Sequential qFunction = new Sequential();
        qFunction.add(new Dense(39, 128));
        qFunction.add(new Tanh());
        qFunction.add(new Dense(128, 1));

        return qFunction;
    }

    @Override
    public Integer chooseNextPokemon(BattleView view)
    {
        // TODO: change this to something more intelligent!

        int    bestIdx   = -1;
        double bestHpFrac = -1.0;

        for (int i = 0; i < this.getMyTeamView(view).size(); ++i)
        {
            PokemonView p = this.getMyTeamView(view).getPokemonView(i);
            if (p.hasFainted()) {
                continue;
            }

            double curHp   = p.getCurrentStat(Stat.HP);
            double maxHp   = p.getInitialStat(Stat.HP);
            double hpFrac  = (maxHp > 0.0) ? (curHp / maxHp) : 0.0;

            if (hpFrac > bestHpFrac) {
                bestHpFrac = hpFrac;
                bestIdx    = i;
            }
        }

        return (bestIdx >= 0) ? bestIdx : null;
    }

    @Override
    public MoveView getMove(BattleView view)
    {
        // TODO: change this to include random exploration during training and maybe use the transition model to make
        // good predictions?
        // if you choose to use the transition model you might want to also override the makeGroundTruth(...) method
        // to not use temporal difference learning

        // currently always tries to argmax the learned model
        // this is not a good idea to always do when training. When playing evaluation games you *do* want to always
        // argmax your model, but when training our model may not know anything yet! So, its a good idea to sometime
        // during training choose *not* to argmax the model and instead choose something new at random.

        // HOW that randomness works and how often you do it are up to you, but it *will* affect the quality of your
        // learned model whether you do it or not!
        moveCount++;
        double frac    = Math.min(1.0, moveCount / 50000.0); // 50k hyperpar,m
        double thresh = Math.max(0.05, 1.0 - frac);

        if (view.getRandom().nextDouble() < thresh) {
            List<MoveView> moves = this.getPotentialMoves(view);
            if (moves == null || moves.isEmpty()) {
                return this.argmax(view);
            }
            int choice = view.getRandom().nextInt(moves.size());
            return moves.get(choice);
        }

        return this.argmax(view);
    }

    @Override
    public void afterGameEnds(BattleView view)
    {

    }

}

