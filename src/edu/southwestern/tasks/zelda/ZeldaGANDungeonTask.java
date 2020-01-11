package edu.southwestern.tasks.zelda;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.util.datastructures.ArrayUtil;

public class ZeldaGANDungeonTask extends ZeldaDungeonTask<ArrayList<Double>>{

	public ZeldaGANDungeonTask() {
		super();
	}
	
	@Override
	public Dungeon getZeldaDungeonFromGenotype(Genotype<ArrayList<Double>> individual) {
		ArrayList<Double> latentVector = individual.getPhenotype();
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		
		
		return null;
	}

}
