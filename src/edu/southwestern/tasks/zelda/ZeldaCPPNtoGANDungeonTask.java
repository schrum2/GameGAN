package edu.southwestern.tasks.zelda;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
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

	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:0","trials:1","mu:10","base:zeldacppntogan","log:ZeldaCPPNtoGAN-Test","saveTo:Test","zeldaGANLevelWidthChunks:5","zeldaGANLevelHeightChunks:5","zeldaGANModel:ZeldaFixedDungeonsAll_5000_10.pth","maxGens:500","io:true","netio:true","GANInputSize:10","mating:true","fs:false","task:edu.southwestern.tasks.zelda.ZeldaCPPNtoGANDungeonTask","cleanOldNetworks:false", "zeldaGANUsesOriginalEncoding:false","allowMultipleFunctions:true","ftype:0","watch:true","netChangeActivationRate:0.3","cleanFrequency:-1","simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
