package edu.southwestern.tasks.zelda;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import me.jakerg.rougelike.RougelikeApp;

public abstract class ZeldaDungeonTask<T> extends NoisyLonerTask<T> {

	public ZeldaDungeonTask() {
		// Objective functions
		MMNEAT.registerFitnessFunction("DistanceToTriforce");
		MMNEAT.registerFitnessFunction("NegativeRooms"); // Fewer rooms means more interesting shapes
		MMNEAT.registerFitnessFunction("PercentRoomsTraversed"); // Avoid superfluous rooms
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
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {
		Dungeon dungeon = getZeldaDungeonFromGenotype(individual);
		int distanceToTriforce = -100; // Very bad fitness if level is not beatable 
		int numRooms = 0;
		int searchStatesVisited = 0;
		int numRoomsTraversed = 0;
		if(dungeon != null) {
			try {
				numRooms = dungeon.getLevels().size();
				// A* should already have been run during creation to assure beat-ability, but it is run again here to get the action sequence.
				ArrayList<GridAction> actionSequence;
				HashSet<ZeldaState> mostRecentVisited;
				//actionSequence = DungeonUtil.makeDungeonPlayable(dungeon);
				Search<GridAction,ZeldaState> search = new AStarSearch<>(ZeldaLevelUtil.manhattan);
				ZeldaState state = new ZeldaState(5, 5, 0, dungeon);
				try {
					actionSequence = ((AStarSearch<GridAction, ZeldaState>) search).search(state, true, Parameters.parameters.integerParameter("aStarSearchBudget"));
					if(actionSequence != null) 
						distanceToTriforce = actionSequence.size();
				}catch(IllegalStateException e) {
					throw e; // Pass on exception, but the finally assures we save states when things go wrong.
				} finally {
					mostRecentVisited = ((AStarSearch<GridAction, ZeldaState>) search).getVisited();
					searchStatesVisited = mostRecentVisited.size();
				}

				HashSet<Pair<Integer,Integer>> visitedRoomCoordinates = new HashSet<>();
				for(ZeldaState zs: mostRecentVisited) {
					// Set does not allow duplicates: one Pair per room
					visitedRoomCoordinates.add(new Pair<>(zs.dX,zs.dY));
				}

				numRoomsTraversed = visitedRoomCoordinates.size();

				if(CommonConstants.watch) {
					System.out.println("Distance to Triforce: "+distanceToTriforce);
					System.out.println("Number of rooms: "+numRooms);
					System.out.println("Number of rooms traversed: "+numRoomsTraversed);
					System.out.println("Number of states visited: "+searchStatesVisited);
					// View whole dungeon layout
					BufferedImage image = DungeonUtil.viewDungeon(dungeon, mostRecentVisited);
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
		return new Pair<double[], double[]>(new double[]{distanceToTriforce, -numRooms, numRooms == 0 ? 0 : (numRoomsTraversed*1.0)/numRooms}, new double[] {numRooms, numRoomsTraversed, searchStatesVisited});
	}
}
