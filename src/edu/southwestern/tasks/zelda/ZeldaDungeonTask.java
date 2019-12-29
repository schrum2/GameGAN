package edu.southwestern.tasks.zelda;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.util.datastructures.Pair;

public abstract class ZeldaDungeonTask<T> extends NoisyLonerTask<T> {

	public ZeldaDungeonTask() {
		// Objective functions
		MMNEAT.registerFitnessFunction("DistanceToTriforce");
		// Additional information tracked about each dungeon
		MMNEAT.registerFitnessFunction("NumRooms",false);
		MMNEAT.registerFitnessFunction("NumRoomsTraversed",false);
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
		
		// TODO: Run A* beat-ability eval
		
		int distanceToTrifoce = -1; // TODO: calculate
		int numRooms = -1;			// TODO: calculate
		int numRoomsTraversed = -1; // TODO: calculate
		
		return new Pair<double[], double[]>(new double[]{distanceToTrifoce}, new double[] {numRooms, numRoomsTraversed});
	}
}
