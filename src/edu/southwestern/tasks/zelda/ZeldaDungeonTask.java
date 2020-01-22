package edu.southwestern.tasks.zelda;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.MultiObjectiveScore;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;

public abstract class ZeldaDungeonTask<T> extends LonerTask<T> {

	public ZeldaDungeonTask() {
		// Objective functions
		if(Parameters.parameters.booleanParameter("zeldaDungeonDistanceFitness")) 
			MMNEAT.registerFitnessFunction("DistanceToTriforce");
		if(Parameters.parameters.booleanParameter("zeldaDungeonFewRoomFitness")) 
			MMNEAT.registerFitnessFunction("NegativeRooms"); // Fewer rooms means more interesting shapes
		if(Parameters.parameters.booleanParameter("zeldaPercentDungeonTraversedRoomFitness")) 
			MMNEAT.registerFitnessFunction("PercentRoomsTraversed"); // Avoid superfluous rooms
		if(Parameters.parameters.booleanParameter("zeldaDungeonTraversedRoomFitness")) 
			MMNEAT.registerFitnessFunction("NumRoomsTraversed"); // Visit as many rooms as possible
		if(Parameters.parameters.booleanParameter("zeldaDungeonRandomFitness")) 
			MMNEAT.registerFitnessFunction("RandomFitness");
		// Additional information tracked about each dungeon
		MMNEAT.registerFitnessFunction("NumRooms",false);
		MMNEAT.registerFitnessFunction("NumRoomsTraversed",false);
		MMNEAT.registerFitnessFunction("NumSearchStatesVisited",false);
		// More?
	}

	@Override
	public int numObjectives() {
		return 1;  
	}

	public int numOtherScores() {
		return 2;
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	public abstract Dungeon getZeldaDungeonFromGenotype(Genotype<T> individual);

	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		// Defines the floor space (excluding walls)
		final int ROWS = 7; // Number of rows to look through
		final int COLUMNS = 12; // Number of columns to look through
		
		Dungeon dungeon = getZeldaDungeonFromGenotype(individual);
		int distanceToTriforce = -100; // Very bad fitness if level is not beatable 
		int numRooms = 0;
		int searchStatesVisited = 0;
		int numRoomsTraversed = 0;
		int waterTileCount = 0;
		int wallTileCount = 0;
		if(dungeon != null) {
			try {
				final Point START = new Point(2, 2);
				// Count occurrence of water and wall tiles in the dungeons for MAP Elites binning
				for(Node room: dungeon.getLevels().values()) {					
					for(int x = START.x; x < START.x+ROWS; x++) {
						for(int y = START.y; y < START.y+COLUMNS; y++) {
							Tile tile = room.level.rougeTiles[y][x];
							if(tile.equals(Tile.WALL)) {
								wallTileCount++;
							} else if(tile.equals(Tile.WATER)) {
								waterTileCount++;
							}
						}
					}
				}
				
				numRooms = dungeon.getLevels().size();
				// A* should already have been run during creation to assure beat-ability, but it is run again here to get the action sequence.
				ArrayList<GridAction> actionSequence;
				HashSet<ZeldaState> solutionPath = null;
				HashSet<ZeldaState> mostRecentVisited;
				//actionSequence = DungeonUtil.makeDungeonPlayable(dungeon);
				Search<GridAction,ZeldaState> search = new AStarSearch<>(ZeldaLevelUtil.manhattan);
				ZeldaState startState = new ZeldaState(5, 5, 0, dungeon);
				try {
					actionSequence = ((AStarSearch<GridAction, ZeldaState>) search).search(startState, true, Parameters.parameters.integerParameter("aStarSearchBudget"));
					if(actionSequence != null) {
						distanceToTriforce = actionSequence.size();
						// Get states in the solution to plot a path
						solutionPath = new HashSet<>();
						ZeldaState currentState = startState;
						solutionPath.add(currentState);
						for(GridAction a : actionSequence) {
							currentState = (ZeldaState) currentState.getSuccessor(a);
							solutionPath.add(currentState);
						}
						
						HashSet<Pair<Integer,Integer>> visitedRoomCoordinates = new HashSet<>();
						for(ZeldaState zs: solutionPath) {
							// Set does not allow duplicates: one Pair per room
							visitedRoomCoordinates.add(new Pair<>(zs.dX,zs.dY));
						}

						numRoomsTraversed = visitedRoomCoordinates.size();
					}
				}catch(IllegalStateException e) {
					throw e; // Pass on exception, but the finally assures we save states when things go wrong.
				} finally {
					mostRecentVisited = ((AStarSearch<GridAction, ZeldaState>) search).getVisited();
					searchStatesVisited = mostRecentVisited.size();
				}

				if(CommonConstants.watch) {
					System.out.println("Distance to Triforce: "+distanceToTriforce);
					System.out.println("Number of rooms: "+numRooms);
					System.out.println("Number of rooms traversed: "+numRoomsTraversed);
					System.out.println("Number of states visited: "+searchStatesVisited);
					// View whole dungeon layout
					BufferedImage image = DungeonUtil.viewDungeon(dungeon, mostRecentVisited, solutionPath);
					String saveDir = FileUtilities.getSaveDirectory();
					int currentGen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
					GraphicsUtil.saveImage(image, saveDir + File.separator + (currentGen == 0 ? "initial" : "gen"+ currentGen) + File.separator + "Dungeon"+individual.getId()+".png");
					System.out.println("Enter 'P' to play, or just press Enter to continue");
					String input = MiscUtil.waitForReadStringAndEnterKeyPress();
					System.out.println("Entered \""+input+"\"");
					if(input.toLowerCase().equals("p")) {
						new Thread() {
							@Override
							public void run() {
								// Repeat dungeon generation to remove visited marks
								Dungeon dungeon = getZeldaDungeonFromGenotype(individual);
								RougelikeApp.startDungeon(dungeon);
							}
						}.start();
						System.out.println("Press enter");
						MiscUtil.waitForReadStringAndEnterKeyPress();
					}
				}
			} catch(IllegalStateException e) {
				// Sometimes this exception occurs from A*. Not sure why, but we can take this to mean the level has a problem and deserves bad fitness.
			}
		}
		
		ArrayList<Double> fitness = new ArrayList<Double>(5);
		if(Parameters.parameters.booleanParameter("zeldaDungeonDistanceFitness")) 
			fitness.add(new Double(distanceToTriforce));
		if(Parameters.parameters.booleanParameter("zeldaDungeonFewRoomFitness")) 
			fitness.add(new Double(-numRooms));
		if(Parameters.parameters.booleanParameter("zeldaPercentDungeonTraversedRoomFitness")) 
			fitness.add(new Double(numRooms == 0 ? 0 : (numRoomsTraversed*1.0)/numRooms));
		if(Parameters.parameters.booleanParameter("zeldaDungeonTraversedRoomFitness")) 
			fitness.add(new Double(numRoomsTraversed));
		if(Parameters.parameters.booleanParameter("zeldaDungeonRandomFitness")) 
			fitness.add(new Double(RandomNumbers.fullSmallRand()));
			
		double[] scores = new double[fitness.size()];
		
		for(int i = 0; i < scores.length; i++) {
			scores[i] = fitness.get(i);
		}
		
		// Assign to the behavior vector before using MAP-Elites
		int maxNumRooms = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks") * Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
		double wallTilePercentage = (wallTileCount*1.0)/(numRooms*ROWS*COLUMNS);
		double waterTilePercentage = (waterTileCount*1.0)/(numRooms*ROWS*COLUMNS);

		int wallTileIndex = (int)(wallTilePercentage*ZeldaMAPElitesBinLabels.TILE_GROUPS); // [0,10), [10,20), [20,30), ... , [80,90), [90,100] <-- Assume 100% of one tile type is impossible
		int waterTileIndex = (int)(waterTilePercentage*ZeldaMAPElitesBinLabels.TILE_GROUPS); // [0,10), [10,20), [20,30), ... , [80,90), [90,100] <-- Assume 100% of one tile type is impossible
		
		double[][][] roomsTraversedAccordingToRoomCount = new double[ZeldaMAPElitesBinLabels.TILE_GROUPS][ZeldaMAPElitesBinLabels.TILE_GROUPS][maxNumRooms+1];
		roomsTraversedAccordingToRoomCount[wallTileIndex][waterTileIndex][numRooms] = numRooms == 0 ? 0 : (numRoomsTraversed*1.0)/numRooms; // Percent rooms traversed
		ArrayList<Double> behaviorVector = ArrayUtil.doubleVectorFromArray(ArrayUtil.flatten3DDoubleArray(roomsTraversedAccordingToRoomCount));
		
		double[] other = new double[] {numRooms, numRoomsTraversed, searchStatesVisited};
		return new MultiObjectiveScore<T>(individual, scores, behaviorVector, other);
	}
}
