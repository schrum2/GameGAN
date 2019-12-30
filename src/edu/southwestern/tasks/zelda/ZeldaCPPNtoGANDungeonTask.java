package edu.southwestern.tasks.zelda;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.util.datastructures.ArrayUtil;

public class ZeldaCPPNtoGANDungeonTask<T extends Network> extends ZeldaDungeonTask<T> {

	public ZeldaCPPNtoGANDungeonTask() {
		super();
	}
	
	@Override
	public Dungeon getZeldaDungeonFromGenotype(Genotype<T> individual) {
		return ZeldaCPPNtoGANLevelBreederTask.cppnToDungeon(individual.getPhenotype(), Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks"), Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks"), ArrayUtil.doubleOnes(ZeldaCPPNtoGANLevelBreederTask.SENSOR_LABELS.length));
	}

}
