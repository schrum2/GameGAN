package edu.southwestern.tasks.gvgai;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.gvgai.player.GVGAINNPlayer;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import gvgai.core.game.Game;
import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLParser;
import gvgai.core.vgdl.VGDLRegistry;
import gvgai.tools.IO;

public class GVGAISinglePlayerTask<T extends Network> extends NoisyLonerTask<T> implements NetworkTask {

	public static final String GAMES_PATH = "data/gvgai/examples/gridphysics/"; // Comes from gvgai.tracks.singlePlayer.Test
	String game;
	int level;
	private Game toPlay;
	private GVGAINNPlayer<T> agent;

	@SuppressWarnings("unchecked")
	public GVGAISinglePlayerTask(){
		VGDLFactory.GetInstance().init();
		VGDLRegistry.GetInstance().init();

		game = Parameters.parameters.stringParameter("gvgaiGame");
		System.out.println("GVG-AI game is " + game);
		level = Parameters.parameters.integerParameter("gvgaiLevel");
		System.out.println("GVG-AI level is " + level);

		String game_file = GAMES_PATH + game + ".txt";
		toPlay = new VGDLParser().parseGame(game_file); // Initialize the game

		try {
			agent = (GVGAINNPlayer<T>) ClassCreation.createObject("gvgaiPlayer");
			// null = do not save file of actions
			// random generator produces random seed : TODO: If deterministic, then always use same seed
			// false = not human
			agent.setup(null, RandomNumbers.randomGenerator.nextInt(), false); 
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("Could not create GVG AI player: " + Parameters.parameters.classParameter("gvgaiPlayer"));
			System.exit(1);
		}


		// Registers the three possible scores;
		// Each Score can be individually selected as a Selection Function or not
		// Defaults to only the Victory score being used for selection

		MMNEAT.registerFitnessFunction("Victory", Parameters.parameters.booleanParameter("gvgaiVictory"));
		MMNEAT.registerFitnessFunction("Score", Parameters.parameters.booleanParameter("gvgaiScore"));
		MMNEAT.registerFitnessFunction("Timestep", Parameters.parameters.booleanParameter("gvgaiTimestep"));
	}

	@Override
	public int numObjectives() {
		int numObjectives = 0;

		if(Parameters.parameters.booleanParameter("gvgaiVictory")) numObjectives++;
		if(Parameters.parameters.booleanParameter("gvgaiScore")) numObjectives++;
		if(Parameters.parameters.booleanParameter("gvgaiTimestep")) numObjectives++;

		return numObjectives;
	}

	@Override
	public int numOtherScores() {
		int numObjectives = 0;

		if(!Parameters.parameters.booleanParameter("gvgaiVictory")) numObjectives++;
		if(!Parameters.parameters.booleanParameter("gvgaiScore")) numObjectives++;
		if(!Parameters.parameters.booleanParameter("gvgaiTimestep")) numObjectives++;

		return numObjectives;
	}

	@Override
	public double getTimeStamp() {
		// Most Tasks don't use the Time Stamp
		return 0;
	}

	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {

		agent.assignNetwork(individual.getPhenotype());
		
		boolean visuals = CommonConstants.watch;
		int playerID = 0;

		String level_file = GAMES_PATH + game + "_lvl" + level + ".txt";
		String[] level = new IO().readFile(level_file);
		int randomSeed = RandomNumbers.randomGenerator.nextInt(); // TODO: Allow to be deterministic

		// Will have 3 Indexes: {victory, score, timestep}; Stores these for every Player, in triplets: [w0,s0,t0,w1,s1,t1,...]
		double[] gvgaiScores = GVGAIUtil.runOneGame(toPlay, level, visuals, agent, randomSeed, playerID);
		toPlay.reset();
		
		// Process the scores
		double[] fitness = new double[numObjectives()];
		double[] otherScores = new double[numOtherScores()];
		int fitIndex = 0;
		int otherIndex = 0;


		if(Parameters.parameters.booleanParameter("gvgaiVictory")){
			fitness[fitIndex++] = gvgaiScores[0]; // Index of the Victory score in gvgaiScores
		}else{
			otherScores[otherIndex++] = gvgaiScores[0];
		}


		if(Parameters.parameters.booleanParameter("gvgaiScore")){
			fitness[fitIndex++] = gvgaiScores[1]; // Index of the Game Score in gvgaiScores
		}else{
			otherScores[otherIndex++] = gvgaiScores[1];
		}


		if(Parameters.parameters.booleanParameter("gvgaiTimestep")){
			fitness[fitIndex++] = gvgaiScores[2]; // Index of the Timestep score in gvgaiScores
		}else{
			otherScores[otherIndex++] = gvgaiScores[2];
		}

		return new Pair<double[], double[]>(fitness, otherScores);
	}

	@Override
	public String[] sensorLabels() {
		return new String[]{"Game Score", "Game Health", "Game Speed", "Game Tick", "BIAS"};
	}

	@Override
	public String[] outputLabels() {
		return new String[]{"Utility"};
	}

	// For testing and troubleshooting
	public static void main(String[] ignore) throws FileNotFoundException, NoSuchMethodException {
		String args = "runNumber:0 randomSeed:0 trials:2 maxGens:500 mu:100 io:false netio:false mating:true task:edu.southwestern.tasks.gvgai.GVGAISinglePlayerTask cleanOldNetworks:true fs:false log:Zelda-ZeldaReactive saveTo:ZeldaReactive gvgaiGame:zelda gvgaiLevel:0 gvgaiPlayer:edu.southwestern.tasks.gvgai.GVGAIReactiveNNPlayer watch:true";
		MMNEAT.main(args.split(" "));
	}

}