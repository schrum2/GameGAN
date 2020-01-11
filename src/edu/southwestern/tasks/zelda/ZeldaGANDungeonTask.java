package edu.southwestern.tasks.zelda;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class ZeldaGANDungeonTask extends ZeldaDungeonTask<ArrayList<Double>>{

	private int segmentLength;

	public ZeldaGANDungeonTask() {
		super();
		segmentLength = GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.NUM_NON_LATENT_INPUTS;
	}
	
	@Override
	public Dungeon getZeldaDungeonFromGenotype(Genotype<ArrayList<Double>> individual) {
		ArrayList<Double> latentVector = individual.getPhenotype();
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		int width = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks");
		int height = Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
		Pair<double[][][],double[][][]> gridRepresentation = ZeldaCPPNtoGANLevelBreederTask.latentVectorGridFromCPPN(new ZeldaDirectGANVectorMatrixBuilder(doubleArray, segmentLength), width, height);
		Dungeon dungeon = ZeldaCPPNtoGANLevelBreederTask.gridDataToDungeon(gridRepresentation.t1, gridRepresentation.t2);
		return dungeon;
	}

}
