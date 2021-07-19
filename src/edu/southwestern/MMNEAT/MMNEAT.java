package edu.southwestern.MMNEAT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.southwestern.data.ResultSummaryUtilities;
import edu.southwestern.evolution.EA;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.ScoreHistory;
import edu.southwestern.evolution.crossover.Crossover;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.CombinedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.evolution.mulambda.MuLambda;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.log.EvalLog;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.MultiplePopulationTask;
import edu.southwestern.tasks.Task;
import edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask;
import edu.southwestern.tasks.gvgai.GVGAISinglePlayerTask;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANLevelTask;
import edu.southwestern.tasks.gvgai.zelda.ZeldaLevelTask;
import edu.southwestern.tasks.gvgai.zelda.study.HumanSubjectStudy2019Zelda;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask;
import edu.southwestern.tasks.interactive.loderunner.LodeRunnerGANLevelBreederTask;
import edu.southwestern.tasks.interactive.mario.MarioCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.interactive.mario.MarioGANLevelBreederTask;
import edu.southwestern.tasks.interactive.mario.MarioLevelBreederTask;
import edu.southwestern.tasks.interactive.megaman.MegaManCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.interactive.megaman.MegaManGANLevelBreederTask;
import edu.southwestern.tasks.loderunner.LodeRunnerGANLevelSequenceTask;
import edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask;
import edu.southwestern.tasks.loderunner.LodeRunnerLevelTask;
import edu.southwestern.tasks.mario.MarioCPPNOrDirectToGANLevelTask;
import edu.southwestern.tasks.mario.MarioCPPNtoGANLevelTask;
import edu.southwestern.tasks.mario.MarioGANLevelTask;
import edu.southwestern.tasks.mario.MarioLevelTask;
import edu.southwestern.tasks.mario.MarioTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.megaman.MegaManCPPNtoGANLevelTask;
import edu.southwestern.tasks.megaman.MegaManGANLevelTask;
import edu.southwestern.tasks.megaman.MegaManLevelTask;
import edu.southwestern.tasks.megaman.levelgenerators.MegaManGANGenerator;
import edu.southwestern.tasks.zelda.ZeldaCPPNOrDirectToGANDungeonTask;
import edu.southwestern.tasks.zelda.ZeldaCPPNtoGANDungeonTask;
import edu.southwestern.tasks.zelda.ZeldaDungeonTask;
import edu.southwestern.tasks.zelda.ZeldaGANDungeonTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomGenerator;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.Statistic;
import wox.serial.Easy;

/**
 * Modular Multiobjective Neuro-Evolution of Augmenting Topologies.
 * 
 * Main class that launches experiments.
 * Running "mvn -U install" will create a SNAPSHOT jar recognized by all
 * batch files.
 * 
 * @author Jacob Schrum
 */
public class MMNEAT {

	public static boolean seedExample = false;
	public static int networkInputs = 0;
	public static int networkOutputs = 0;
	public static int modesToTrack = 0;
	public static double[] lowerInputBounds;
	public static double[] upperInputBounds;
	// Real-valued bounds
	public static double[] lower = null; // Lowest allowable value for each gene position
	public static double[] upper = null; // Highest allowable value for each gene position
	
	public static int[] discreteCeilings;
	public static Experiment experiment;
	public static Task task;
	public static EA ea;
	@SuppressWarnings("rawtypes") // could hold any type, depending on command line
	public static Genotype genotype;
	@SuppressWarnings("rawtypes") // could hold any type, depending on command line
	public static ArrayList<Genotype> genotypeExamples;
	@SuppressWarnings("rawtypes") // can crossover any type, depending on command line
	public static Crossover crossoverOperator;
//	public static FunctionOptimizationSet fos;
//	public static RLGlueEnvironment rlGlueEnvironment;
	
//	public static ArrayList<Metaheuristic> metaheuristics;
	public static ArrayList<ArrayList<String>> fitnessFunctions;
	public static ArrayList<Statistic> aggregationOverrides;
//	public static TaskSpec tso;
//	public static FeatureExtractor rlGlueExtractor;
	public static boolean blueprints = false;
	
//	public static PerformanceLog performanceLog;
//	public static MsPacManControllerInputOutputMediator pacmanInputOutputMediator;
	private static ArrayList<Integer> actualFitnessFunctions;
//	public static MsPacManModeSelector pacmanMultitaskScheme = null;
//	public static VariableDirectionBlock directionalSafetyFunction;
	public static EvalLog evalReport = null;
	public static RandomGenerator weightPerturber = null;
	public static MMNEATLog ghostLocationsOnPowerPillEaten = null;
	public static boolean browseLineage = false;
//	public static SubstrateCoordinateMapping substrateMapping = null;
	@SuppressWarnings("rawtypes")
	public static Archive pseudoArchive;
	public static boolean usingDiversityBinningScheme = false;
	
	public static MMNEAT mmneat;

	@SuppressWarnings("rawtypes")
	public static BinLabels getArchiveBinLabelsClass() {
		if (pseudoArchive != null) {
			return pseudoArchive.getBinLabelsClass();
		} else if (ea instanceof MAPElites) {
			return ((MAPElites) ea).getBinLabelsClass();
		}
		throw new IllegalStateException("Attempted to get archive bin label class without using MAP Elites or a psuedo-archive");
	}
	
	@SuppressWarnings("rawtypes")
	public static Archive getArchive() {
		if (pseudoArchive != null) {
			return pseudoArchive;
		} else if (ea instanceof MAPElites) {
			return ((MAPElites) ea).getArchive();
		}
		throw new IllegalStateException("Attempted to get archive without using MAP Elites or a psuedo-archive");
		
	}
	
	
	@SuppressWarnings("rawtypes")
	public static ArrayList<String> fitnessPlusMetaheuristics(int pop) {
		@SuppressWarnings("unchecked")
		ArrayList<String> result = (ArrayList<String>) fitnessFunctions.get(pop).clone();
		return result;
	}

	private static void setupSaveDirectory() {
		String saveTo = Parameters.parameters.stringParameter("saveTo");
		if (Parameters.parameters.booleanParameter("io") && !saveTo.isEmpty()) {
			String directory = FileUtilities.getSaveDirectory();
			File dir = new File(directory);
			if (!dir.exists()) {
				dir.mkdir();
			}
		}
	}

	@SuppressWarnings("rawtypes") // type of genotypes being crossed could be anything
	private static void setupCrossover() throws NoSuchMethodException {
		// Crossover operator
		if (Parameters.parameters.booleanParameter("mating")) {
			crossoverOperator = (Crossover) ClassCreation.createObject("crossover");
		}
	}

	private static void setupTWEANNGenotypeDataTracking(boolean coevolution) {
		if (genotype instanceof TWEANNGenotype || 
				genotype instanceof CombinedGenotype || // Assume first member of pair is TWEANNGenotype
				genotype instanceof CPPNOrDirectToGANGenotype) { // Assume first form is TWEANNGenotype 
			if (Parameters.parameters.booleanParameter("io")
					&& Parameters.parameters.booleanParameter("logTWEANNData")) {
				System.out.println("Init TWEANN Log");
				EvolutionaryHistory.initTWEANNLog();
			}
			if (!coevolution) {
				EvolutionaryHistory.initArchetype(0);
			}

			@SuppressWarnings("rawtypes")
			long biggestInnovation = genotype instanceof CPPNOrDirectToGANGenotype ?
					((TWEANNGenotype) ((CPPNOrDirectToGANGenotype) genotype).getCurrentGenotype()).biggestInnovation():
						(genotype instanceof CombinedGenotype ? 
								((TWEANNGenotype) ((CombinedGenotype) genotype).t1).biggestInnovation() :
									( ((TWEANNGenotype) genotype).biggestInnovation()));
					
			if (biggestInnovation > EvolutionaryHistory.largestUnusedInnovationNumber) {
					EvolutionaryHistory.setInnovation(biggestInnovation + 1);
			}
		}
	}

	public static void prepareCoevolutionArchetypes() {
		for (int i = 0; i < genotypeExamples.size(); i++) {
			String archetypeFile = Parameters.parameters.stringParameter("seedArchetype" + (i + 1));
			if (!EvolutionaryHistory.archetypeFileExists(i) && archetypeFile != null && !archetypeFile.isEmpty()) {
				System.out.println("Using seed archetype: " + archetypeFile);
				EvolutionaryHistory.initArchetype(i, archetypeFile);
			} else {
				System.out.println("New or resumed archetype");
				EvolutionaryHistory.initArchetype(i);
			}
			System.out.println("---------------------------------------------");
		}
	}

	/**
	 * Constructor takes the command line parameters
	 * to initialize the systems parameter values.
	 * @param args directly from command line
	 */
	public MMNEAT(String[] args) {
		Parameters.initializeParameterCollections(args);
	}

	/**
	 * Constructor that takes a parameter file
	 * string to initialize the systems
	 * parameter values.
	 * @param parameterFile
	 */
	public MMNEAT(String parameterFile) {
		Parameters.initializeParameterCollections(parameterFile);
	}

	public static void registerFitnessFunction(String name) {
		registerFitnessFunction(name, 0);
	}

	/**
	 * For plotting purposes. Let simulation know that a given fitness function
	 * will be tracked.
	 * @param name Name of fitness function in plot files
	 * @param pop population index (for coevolution)
	 */
	public static void registerFitnessFunction(String name, int pop) {
		registerFitnessFunction(name, null, true, pop);
	}

	public static void registerFitnessFunction(String name, boolean affectsSelection) {
		registerFitnessFunction(name, affectsSelection, 0);
	}


	/**
	 * Like above, but indicating that the "fitness" function does not affect 
	 * selection means that it is simply an other score that is being tracked
	 * in the logs.
	 * @param name Name of score
	 * @param affectsSelection Whether or not score is actually used for selection
	 * @param pop population index (for coevolution)
	 */
	public static void registerFitnessFunction(String name, boolean affectsSelection, int pop) {
		registerFitnessFunction(name, null, affectsSelection, pop);
	}

	public static void registerFitnessFunction(String name, Statistic override, boolean affectsSelection) {
		registerFitnessFunction(name, override, affectsSelection, 0);
	}

	/**
	 * As above, but it is now possible to indicate how the score is statistically
	 * summarized when noisy evaluations occur. The default is to average scores
	 * across evaluations, but if an overriding statistic is used, then this will
	 * also be mentioned in the log.
	 * @param name Name of score column
	 * @param override Statistic applied across evaluations (null is default/average)
	 * @param affectsSelection whether it affects selection
	 * @param pop population index (for coevolution)
	 */
	public static void registerFitnessFunction(String name, Statistic override, boolean affectsSelection, int pop) {
		if(actualFitnessFunctions == null){
			actualFitnessFunctions = new ArrayList<Integer>();
		}
		while(actualFitnessFunctions.size() <= pop){
			actualFitnessFunctions.add(0);
		}
		if (affectsSelection) {
			int num = actualFitnessFunctions.get(pop) + 1;
			actualFitnessFunctions.set(pop, num);
		}
		// For coevolution.
		// Create enough objective arrays to accomadate each population
		while(fitnessFunctions.size() <= pop) {
			fitnessFunctions.add(new ArrayList<String>());
		}
		fitnessFunctions.get(pop).add(name);
		aggregationOverrides.add(override);
	}

	/**
	 * Load important classes from class parameters.
	 * Other important experiment setup also occurs.
	 * Perhaps the most important classes that always
	 * need to be loaded at the task, the experiment, 
	 * and the ea. These get stored in public static 
	 * variables of this class so they are easily accessible
	 * from all parts of the code.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void loadClasses() {
		try {
			ActivationFunctions.resetFunctionSet();
			setupSaveDirectory();

			fitnessFunctions = new ArrayList<ArrayList<String>>();
			fitnessFunctions.add(new ArrayList<String>());
			aggregationOverrides = new ArrayList<Statistic>();

			boolean loadFrom = !Parameters.parameters.stringParameter("loadFrom").equals("");
			System.out.println("Init Genotype Ids");
			EvolutionaryHistory.initGenotypeIds();
			weightPerturber = (RandomGenerator) ClassCreation.createObject("weightPerturber");

			setupCrossover();

			// A task is always required
			System.out.println("Set Task");
			// modesToTrack has to be set before task initialization
			int multitaskModes = CommonConstants.multitaskModules;
			if (multitaskModes > 1) {
				modesToTrack = multitaskModes;
			}
			
			task = (Task) ClassCreation.createObject("task");
			System.out.println("Load task: " + task);
			boolean multiPopulationCoevolution = false;
			// For all types of Ms Pac-Man tasks
			if (Parameters.parameters.booleanParameter("scalePillsByGen")
					&& Parameters.parameters.stringParameter("lastSavedDirectory").equals("")
					&& Parameters.parameters.integerParameter("lastSavedGeneration") == 0) {
				System.out.println("Set pre-eaten pills high, since we are scaling pills with generation");
				Parameters.parameters.setDouble("preEatenPillPercentage", 0.999);
			}

			if (task instanceof GVGAISinglePlayerTask) {
				GVGAISinglePlayerTask temp = (GVGAISinglePlayerTask) task;
				setNNInputParameters(temp.sensorLabels().length, temp.outputLabels().length);
			} else if(task instanceof InteractiveEvolutionTask) {
				System.out.println("set up Interactive Evolution Task");
				InteractiveEvolutionTask temp = (InteractiveEvolutionTask) task;
				// Since these tasks use real-vector genotypes, to not set the NN params
				//if(!(temp instanceof MarioGANLevelBreederTask) && !(temp instanceof ZeldaGANLevelBreederTask)) setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
				if(!(temp instanceof InteractiveGANLevelEvolutionTask)) setNNInputParameters(temp.numCPPNInputs(), temp.numCPPNOutputs());
			} else if (task instanceof MarioTask) {
				setNNInputParameters(((Parameters.parameters.integerParameter("marioInputWidth") * Parameters.parameters.integerParameter("marioInputHeight")) * 2) + 1, MarioTask.MARIO_OUTPUTS); //hard coded for now, 5 button outputs
				System.out.println("Set up Mario Task");
			} else if (task instanceof MarioLevelTask) {
				GANProcess.type = GANProcess.GAN_TYPE.MARIO;
				if(task instanceof MarioCPPNtoGANLevelTask|| task instanceof MarioCPPNOrDirectToGANLevelTask) {
					// Evolving CPPNs that create latent vectors that are sent to a GAN
					setNNInputParameters(MarioCPPNtoGANLevelBreederTask.UPDATED_INPUTS.length, GANProcess.latentVectorLength());
				} else {
					// This line only matters for the CPPN version of the task, but doesn't hurt the GAN version, which does not evolve networks
					setNNInputParameters(MarioLevelBreederTask.INPUTS.length, MarioLevelBreederTask.OUTPUTS.length);
				}
				System.out.println("Set up Mario Level Task");
			} else if (task instanceof ZeldaDungeonTask) { // Full dungeons using the Rogue-like engine
				GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
				if(task instanceof ZeldaCPPNtoGANDungeonTask || task instanceof ZeldaCPPNOrDirectToGANDungeonTask) {
					// Evolving CPPNs that create latent vectors that are sent to a GAN
					setNNInputParameters(ZeldaCPPNtoGANLevelBreederTask.SENSOR_LABELS.length, GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables());
				}
			} else if (task instanceof ZeldaLevelTask){ // For evolving Zelda levels in GVG-AI ... not well developed
				GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
				System.out.println("Set up Zelda Level Task");
			} else if (task instanceof MegaManLevelTask){
				GANProcess.type = GANProcess.GAN_TYPE.MEGA_MAN;
				// Ok to set the CPPN input parameters even if they are not used
				setNNInputParameters(MegaManCPPNtoGANLevelBreederTask.SENSOR_LABELS.length, MegaManCPPNtoGANLevelBreederTask.staticNumCPPNOutputs());
				System.out.println("Set up Mega Man Task");
			} else if(task instanceof LodeRunnerLevelTask) {
				GANProcess.type = GANProcess.GAN_TYPE.LODE_RUNNER;
				System.out.println("Set up Lode Runner Task");
			} else if (task == null) {
				// this else statement should only happen for JUnit testing cases.
				// Some default network setup is needed.
				System.out.println("No task defined! It is assumed that this is part of a JUnit test.");
				setNNInputParameters(5, 3);
			} else {
				System.out.println("A valid task must be specified!");
				System.out.println(task);
				System.exit(1);
			}

			// Only loads if settings indicate that this should be used
			ScoreHistory.load();

			// An EA is always needed. Currently only GenerationalEA classes are supported
			if (!loadFrom) {
				System.out.println("Create EA");
				ea = (EA) ClassCreation.createObject("ea");
			}
			// A Genotype to evolve with is always needed
			System.out.println("Example genotype");
			String seedGenotype = Parameters.parameters.stringParameter("seedGenotype");
			if (seedGenotype.isEmpty()) {
				genotype = (Genotype) ClassCreation.createObject("genotype");
			} else {
				// Copy assures a fresh genotype id
				System.out.println("Loading seed genotype: " + seedGenotype);
				genotype = ((Genotype) Easy.load(seedGenotype)).copy();
				// System.out.println(genotype);
				seedExample = true;
			}
			setupTWEANNGenotypeDataTracking(multiPopulationCoevolution);
			
			if (Parameters.parameters.booleanParameter("trackPseudoArchive")) {
				usingDiversityBinningScheme = true;
				// Create a pseudo archive for use with objective evolution TODO
				pseudoArchive = new Archive<>(Parameters.parameters.booleanParameter("netio"), Parameters.parameters.stringParameter("archiveSubDirectoryName"));
				int startSize = Parameters.parameters.integerParameter("mu");
				ArrayList<Genotype> startingPopulation = PopulationUtil.initialPopulation(genotype.newInstance(),startSize);
				for (Genotype g : startingPopulation) {
					System.out.println("genotype: " + g);
					Score s = ((LonerTask) task).evaluate(g);
					System.out.println("score: " + s);
					pseudoArchive.add(s); // Fill the archive with random starting individuals
				}
				if (ea instanceof MuLambda)
					((MuLambda) ea).setUpPseudoArchive();
			}
			System.out.println("Create Experiment");
			experiment = (Experiment) ClassCreation.createObject("experiment");
			experiment.init();
			if (!loadFrom && Parameters.parameters.booleanParameter("io")) {
				if (Parameters.parameters.booleanParameter("logMutationAndLineage")) {
					EvolutionaryHistory.initLineageAndMutationLogs();
				}
			}
		} catch (Exception ex) {
			System.out.println("Exception: " + ex);
			ex.printStackTrace();
		}
	}

	/**
	 * Resets the classes used in MMNEAT and and sets them to null.
	 */
	public static void clearClasses() {
		task = null;
		fitnessFunctions = null;
		aggregationOverrides = null;
		ea = null;
		genotype = null;
		experiment = null;
		EvolutionaryHistory.archetypes = null;
	}

	/**
	 * Initializes and runs the experiment given the loaded classes.
	 */
	public void run() {
		System.out.println("Run:");
		clearClasses();
		loadClasses();

		if (Parameters.parameters.booleanParameter("io")) {
			Parameters.parameters.saveParameters();
		}
		System.out.println("Run");
		experiment.run();
		System.out.println("Experiment finished");
		GANProcess.terminateGANProcess();
	}

	/**
	 * Processes the task experiment for a given number of runs.
	 * @param runs
	 * @throws FileNotFoundException
	 * @throws NoSuchMethodException
	 */
	public static void process(int runs) throws FileNotFoundException, NoSuchMethodException {
		try {
			task = (Task) ClassCreation.createObject("task");
		} catch (NoSuchMethodException ex) {
			System.out.println("Failed to instantiate task " + Parameters.parameters.classParameter("task"));
			System.exit(1);
		}
		String base = Parameters.parameters.stringParameter("base");
		String saveTo = Parameters.parameters.stringParameter("saveTo");
		int run = Parameters.parameters.integerParameter("runNumber");
		if(task instanceof MultiplePopulationTask) {
			String runDir = base + "/" + saveTo + run + "/";
			int i = 0;
			// Note: Only works for populations of evolved TWEANNs,
			// because an archetype is required. There will be one
			// archetype file for each population, so checking for the
			// existence of the files verifies the number of populations.
			while(new File(runDir + "archetype"+i+".xml").exists()) {
				ResultSummaryUtilities.processExperiment(
						base + "/" + saveTo,
						Parameters.parameters.stringParameter("log"), runs, Parameters.parameters.integerParameter("maxGens"),
						"_" + ("pop" + i) + "parents_log.txt",
						"_" + ("pop" + i) + "parents_gen",
						base, i);
				i++;
			}
		} else { //for lonerTask sending in default of population 0
			ResultSummaryUtilities.processExperiment(
					base + "/" + saveTo,
					Parameters.parameters.stringParameter("log"), runs, Parameters.parameters.integerParameter("maxGens"),
					"_" + (task instanceof MultiplePopulationTask ? "pop0" : "") + "parents_log.txt",
					"_" + (task instanceof MultiplePopulationTask ? "pop0" : "") + "parents_gen",
					base, 0);
		}
	}

	/**
	 * Processes the hypervolume(HV) for a given number of runs.
	 * @param runs
	 * @throws FileNotFoundException
	 */
	public static void calculateHVs(int runs) throws FileNotFoundException {
		try {
			task = (Task) ClassCreation.createObject("task");
		} catch (NoSuchMethodException ex) {
			Logger.getLogger(MMNEAT.class.getName()).log(Level.SEVERE, null, ex);
		}
		ResultSummaryUtilities.hypervolumeProcessing(
				Parameters.parameters.stringParameter("base") + "/" + Parameters.parameters.stringParameter("saveTo"),
				runs, Parameters.parameters.stringParameter("log"),
				"_" + (task instanceof MultiplePopulationTask ? "pop0" : "") + "parents_gen",
				Parameters.parameters.integerParameter("maxGens"), Parameters.parameters.stringParameter("base"));
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Simple way of debugging using the profiler
		//args = new String[]{"runNumber:1", "randomSeed:0", "base:tetris", "logPerformance:false", "logTWEANNData:false", "trials:1", "maxGens:300", "mu:50", "io:true", "netio:true", "mating:true", "task:edu.southwestern.tasks.rlglue.tetris.TetrisTask", "rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris", "rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor", "tetrisTimeSteps:true", "tetrisBlocksOnScreen:false", "rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent", "splitRawTetrisInputs:true", "senseHolesDifferently:true", "log:Tetris-moRawHNSeedFixedSplitInputs", "saveTo:moRawHNSeedFixedSplitInputs", "hyperNEATSeedTask:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask", "substrateMapping:edu.southwestern.networks.hyperneat.BottomSubstrateMapping", "HNTTetrisProcessDepth:1", "netLinkRate:0.0", "netSpliceRate:0.0", "linkExpressionThreshold:-1"};

		if (args.length == 0) {
			System.out.println("First command line parameter must be one of the following:");
			System.out.println("\tmultiple:n\twhere n is the number of experiments to run in sequence");
			System.out.println("\trunNumber:n\twhere n is the specific experiment number to assign");
			System.out.println("\tprocess:n\twhere n is the number of experiments to do data processing on");
			System.out.println("\tlineage:n\twhere n is the experiment number to do lineage browsing on");
			System.exit(0);
		}
		long start = System.currentTimeMillis();
		// Executor.main(args);
		StringTokenizer st = new StringTokenizer(args[0], ":");
		if (args[0].startsWith("multiple:")) {
			st.nextToken(); // "multiple"
			String value = st.nextToken();

			int runs = Integer.parseInt(value);
			for (int i = 0; i < runs; i++) {
				args[0] = "runNumber:" + i;
				evolutionaryRun(args);
			}
			process(runs);
		} else if (args[0].startsWith("hv:")) {
			st.nextToken(); // "hv"
			String value = st.nextToken();

			int runs = Integer.parseInt(value);
			args[0] = "runNumber:0";
			Parameters.initializeParameterCollections(args); // file should exist
			loadClasses();
			calculateHVs(runs);
		} else if (args[0].startsWith("lineage:")) {
			System.out.println("Lineage browser");
			browseLineage = true;
			st.nextToken(); // "lineage"
			String value = st.nextToken();

			int run = Integer.parseInt(value);
			args[0] = "runNumber:" + run;
			Parameters.initializeParameterCollections(args); // file should exist			
			System.out.println("Params loaded");
			String saveTo = Parameters.parameters.stringParameter("saveTo");
			String loadFrom = Parameters.parameters.stringParameter("loadFrom");
			boolean includeChildren = false;
			if (loadFrom == null || loadFrom.equals("")) {
				loadFrom = saveTo;
				includeChildren = true;
			}
			Offspring.fillInLineage(Parameters.parameters.stringParameter("base"), saveTo, run,
					Parameters.parameters.stringParameter("log"), loadFrom, includeChildren);
			Offspring.browse();
		} else if (args[0].startsWith("process:")) {
			st.nextToken(); // "process"
			String value = st.nextToken();

			int runs = Integer.parseInt(value);
			args[0] = "runNumber:0";
			Parameters.initializeParameterCollections(args); // file should exist
			loadClasses();
			process(runs);
		} else if(args[0].startsWith("zeldaType:")){
			
			Parameters.initializeParameterCollections(args);
			String type = Parameters.parameters.stringParameter("zeldaType");
			HumanSubjectStudy2019Zelda.Type t = null;
			switch(type) {
			case "original":
				t = HumanSubjectStudy2019Zelda.Type.ORIGINAL;
				break;
			case "generated":
				t = HumanSubjectStudy2019Zelda.Type.GENERATED_DUNGEON;
				break;
			case "tutorial":
				t = HumanSubjectStudy2019Zelda.Type.TUTORIAL;
				break;
			default:
				throw new IllegalArgumentException("zeldaType : " + type + " unrecognized. (original, generated, tutorial)");
			}
			HumanSubjectStudy2019Zelda.runTrial(t);
		} else {
			evolutionaryRun(args);
		}
		System.out.println("done: " + (((System.currentTimeMillis() - start) / 1000.0) / 60.0) + " minutes");
		System.exit(0);
	}

	/**
	 * Runs a single evolution experiment with a given run number.
	 * @param args Command line parameters
	 */
	private static void evolutionaryRun(String[] args) {
		// Commandline
		mmneat = new MMNEAT(args);
		String branchRoot = Parameters.parameters.stringParameter("branchRoot");
		String lastSavedDirectory = Parameters.parameters.stringParameter("lastSavedDirectory");
		if (branchRoot != null && !branchRoot.isEmpty()
				&& (lastSavedDirectory == null || lastSavedDirectory.isEmpty())) {
			// This run is a branch off of another run.
			Parameters rootParameters = new Parameters(new String[0]);
			System.out.println("Loading root parameters from " + branchRoot);
			rootParameters.loadParameters(branchRoot);
			// Copy the needed parameters
			Parameters.parameters.setString("lastSavedDirectory", rootParameters.stringParameter("lastSavedDirectory"));
			Parameters.parameters.setString("archetype", rootParameters.stringParameter("archetype"));
			Parameters.parameters.setLong("lastInnovation", rootParameters.longParameter("lastInnovation"));
			Parameters.parameters.setLong("lastGenotypeId", rootParameters.longParameter("lastGenotypeId"));
		}
		RandomNumbers.reset();

		mmneat.run();

		closeLogs();
	}

	/**
	 * Checks for logs that aren't null, closes them and sets them to null.
	 */
	public static void closeLogs() {
		if (EvolutionaryHistory.tweannLog != null) {
			EvolutionaryHistory.tweannLog.close();
			EvolutionaryHistory.tweannLog = null;
		}
		if (EvolutionaryHistory.mutationLog != null) {
			EvolutionaryHistory.mutationLog.close();
			EvolutionaryHistory.mutationLog = null;
		}
		if (EvolutionaryHistory.lineageLog != null) {
			EvolutionaryHistory.lineageLog.close();
			EvolutionaryHistory.lineageLog = null;
		}
	}

	/**
	 * Signals that neural networks will be used, and sets them up
	 * 
	 * @param numIn
	 *            Number of task inputs to network
	 * @param numOut
	 *            Number of task outputs to network (not counting extra modes)
	 */
	public static void setNNInputParameters(int numIn, int numOut) {
		networkInputs = numIn;
		networkOutputs = numOut;
		int multitaskModes = CommonConstants.multitaskModules;
		networkOutputs *= multitaskModes;
		System.out.println("Networks will have " + networkInputs + " inputs and " + networkOutputs + " outputs.");

		lowerInputBounds = new double[networkInputs];
		upperInputBounds = new double[networkInputs];
		for (int i = 0; i < networkInputs; i++) {
			lowerInputBounds[i] = -1.0;
			upperInputBounds[i] = 1.0;
		}
	}

	/**
	 * This method only applies to bounded real-valued genotypes.
	 * Bounded real-valued genotypes are currently only used in two types of domains.
	 * @return
	 */
	public static double[] getLowerBounds() {
		// For Mario GAN, the latent vector length determines the size, but the lower bounds are all zero
		if(task instanceof MarioGANLevelTask || task instanceof MarioGANLevelBreederTask|| task instanceof MarioCPPNOrDirectToGANLevelTask) return ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength() * Parameters.parameters.integerParameter("marioGANLevelChunks")); // all -1
		// Similar for ZeldaGAN
		else if(task instanceof ZeldaGANLevelBreederTask || task instanceof ZeldaGANLevelTask) return ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength()); // all -1
		else if(task instanceof ZeldaGANDungeonTask) return ArrayUtil.doubleNegativeOnes(ZeldaGANDungeonTask.genomeLength()); // all -1
		else if(task instanceof ZeldaCPPNOrDirectToGANDungeonTask) return ArrayUtil.doubleNegativeOnes(ZeldaGANDungeonTask.genomeLength()); // all -1
		else if(task instanceof LodeRunnerGANLevelBreederTask || task instanceof LodeRunnerGANLevelTask) return ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength());
		else if(task instanceof LodeRunnerGANLevelSequenceTask) return ArrayUtil.doubleNegativeOnes(GANProcess.latentVectorLength() * Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence")); 
		else if(task instanceof MegaManGANLevelBreederTask || task instanceof MegaManGANLevelTask || task instanceof MegaManCPPNtoGANLevelBreederTask|| task instanceof MegaManCPPNtoGANLevelTask) return ArrayUtil.doubleNegativeOnes((Parameters.parameters.integerParameter("GANInputSize") + MegaManGANGenerator.numberOfAuxiliaryVariables()) * Parameters.parameters.integerParameter("megaManGANLevelChunks"));
		else {
			throw new IllegalArgumentException("BoundedRealValuedGenotypes only supported for Function Optimization and Mario/Zelda/LodeRuner/MegaMan GAN");
		}
	}

	/**
	 * Similar to the lower bounds method above. Only used
	 * for two domains, currently.
	 * @return
	 */
	public static double[] getUpperBounds() {
		if(upper != null) return upper;
		
		if(fos != null) 
			upper = fos.getUpperBounds();
		else if(task instanceof ShapeInnovationTask) 
			upper = new double[]{1,1,1,1,1}; // Background color (first three) and pitch, heading
		else if(task instanceof ImageMatchTask || task instanceof PictureTargetTask || task instanceof PicbreederTask || task instanceof AnimationBreederTask) 
			upper = new double[] {Parameters.parameters.doubleParameter("maxScale"), 2*Math.PI, Parameters.parameters.doubleParameter("imageCenterTranslationRange"), Parameters.parameters.doubleParameter("imageCenterTranslationRange")};
		else if(task instanceof FunctionOptimizationTask) 
			upper = ArrayUtil.doubleSpecified(Parameters.parameters.integerParameter("foVectorLength"), Parameters.parameters.doubleParameter("foUpperBounds")); 
		else if(task instanceof MarioGANLevelTask || task instanceof MarioGANLevelBreederTask||task instanceof MarioCPPNOrDirectToGANLevelTask) 
			upper = ArrayUtil.doubleOnes(GANProcess.latentVectorLength() * Parameters.parameters.integerParameter("marioGANLevelChunks")); // all ones
		else if(task instanceof ZeldaGANLevelBreederTask || task instanceof ZeldaGANLevelTask) 
			upper = ArrayUtil.doubleOnes(GANProcess.latentVectorLength()); // all ones
		else if(task instanceof ZeldaGANDungeonTask) 
			upper = ArrayUtil.doubleOnes(ZeldaGANDungeonTask.genomeLength()); // all ones
		else if(task instanceof ZeldaCPPNOrDirectToGANDungeonTask) 
			upper = ArrayUtil.doubleOnes(ZeldaGANDungeonTask.genomeLength()); // all ones
		else if(task instanceof LodeRunnerGANLevelBreederTask || task instanceof LodeRunnerGANLevelTask) 
			upper = ArrayUtil.doubleOnes(GANProcess.latentVectorLength());
		else if(task instanceof LodeRunnerGANLevelSequenceTask) 
			upper = ArrayUtil.doubleOnes(GANProcess.latentVectorLength() * Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence")); 
		else if(task instanceof MegaManGANLevelBreederTask || task instanceof MegaManGANLevelTask || task instanceof MegaManCPPNtoGANLevelBreederTask|| task instanceof MegaManCPPNtoGANLevelTask) 
			upper = ArrayUtil.doubleOnes((Parameters.parameters.integerParameter("GANInputSize") + MegaManGANGenerator.numberOfAuxiliaryVariables()) * Parameters.parameters.integerParameter("megaManGANLevelChunks"));
		else {
			throw new IllegalArgumentException("BoundedRealValuedGenotypes only supported for Function Optimization and Mario/Zelda/LodeRunner/MegaMan GAN");
		}
		
		return upper;
	}
}
