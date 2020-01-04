package edu.southwestern.tasks.zelda;

import java.util.ArrayList;
import java.util.HashSet;

import edu.southwestern.MMNEAT.MMNEAT;
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
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;
import me.jakerg.rougelike.RougelikeApp;

public abstract class ZeldaDungeonTask<T> extends NoisyLonerTask<T> {

	public ZeldaDungeonTask() {
		// Objective functions
		MMNEAT.registerFitnessFunction("DistanceToTriforce");
		MMNEAT.registerFitnessFunction("NegativeRooms"); // Fewer rooms means more interesting shapes
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
		if(dungeon == null) {
			// The A* fix could not make the level beat-able. This probably means that the state space was too big to search,
			// so this level should receive minimal fitness.
			return new Pair<double[], double[]>(new double[]{-100, 0}, new double[] {0, 0, 0});
		}
		int numRooms = dungeon.getLevels().size();
		int searchStatesVisited = 0;
		// A* should already have been run during creation to assure beat-ability, but it is run again here to get the action sequence.
		ArrayList<GridAction> actionSequence;
		HashSet<ZeldaState> mostRecentVisited;
		try {
			//			actionSequence = DungeonUtil.makeDungeonPlayable(dungeon);
			Search<GridAction,ZeldaState> search = new AStarSearch<>(ZeldaLevelUtil.manhattan);
			ZeldaState state = new ZeldaState(5, 5, 0, dungeon);
			try {
				actionSequence = ((AStarSearch<GridAction, ZeldaState>) search).search(state, true, Parameters.parameters.integerParameter("aStarSearchBudget"));
			}catch(IllegalStateException e) {
				throw e; // Pass on exception, but the finally assures we save states when things go wrong.
			} finally {
				mostRecentVisited = ((AStarSearch<GridAction, ZeldaState>) search).getVisited();
				searchStatesVisited = mostRecentVisited.size();
			}


		} catch(IllegalStateException e) {
			// But sometimes this exception occurs anyway. Not sure why, but we can take this to mean the level has a problem and deserves bad fitness
			return new Pair<double[], double[]>(new double[]{-100, -numRooms}, new double[] {numRooms, 0, searchStatesVisited});
		}

		int distanceToTriforce = actionSequence == null ? -100 : actionSequence.size();

		HashSet<Pair<Integer,Integer>> visitedRoomCoordinates = new HashSet<>();
		for(ZeldaState zs: mostRecentVisited) {
			// Set does not allow duplicates: one Pair per room
			visitedRoomCoordinates.add(new Pair<>(zs.dX,zs.dY));
		}

		int numRoomsTraversed = visitedRoomCoordinates.size();

		if(CommonConstants.watch) {
			System.out.println("Distance to Triforce: "+distanceToTriforce);
			System.out.println("Number of rooms: "+numRooms);
			System.out.println("Number of rooms traversed: "+numRoomsTraversed);
			System.out.println("Number of states visited: "+searchStatesVisited);
			// View whole dungeon layout
			DungeonUtil.viewDungeon(dungeon, mostRecentVisited);			
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

		return new Pair<double[], double[]>(new double[]{distanceToTriforce, -numRooms}, new double[] {numRooms, numRoomsTraversed, searchStatesVisited});
	}
}
