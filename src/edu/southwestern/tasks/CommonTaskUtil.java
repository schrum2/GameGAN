package edu.southwestern.tasks;

import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.lineage.Offspring;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.Plot;

public class CommonTaskUtil {

	public static final int NETWORK_WINDOW_OFFSET = 0;

	public static List<DrawingPanel> lastSubstrateWeightPanelsReturned = null;
	
	public static Pair<DrawingPanel, DrawingPanel> getDrawingPanels(Genotype<?> genotype){

		DrawingPanel panel = null;
		DrawingPanel cppnPanel = null;

		if (genotype instanceof TWEANNGenotype) {
			if (CommonConstants.showNetworks) {
				panel = new DrawingPanel(TWEANN.NETWORK_VIEW_DIM, TWEANN.NETWORK_VIEW_DIM, "Evolved Network "+genotype.getId());
				panel.setLocation(NETWORK_WINDOW_OFFSET, 0);
				TWEANN network = ((TWEANNGenotype) genotype).getPhenotype();
				//System.out.println("Draw network with " + network.numInputs() + " inputs");
				network.draw(panel);
			}
			if (CommonConstants.viewModePreference && TWEANN.preferenceNeuronPanel == null && TWEANN.preferenceNeuron()) {
				TWEANN.preferenceNeuronPanel = new DrawingPanel(Plot.BROWSE_DIM, Plot.BROWSE_DIM, "Preference Neuron Activation");
				TWEANN.preferenceNeuronPanel.setLocation(Plot.BROWSE_DIM + Plot.EDGE, Plot.BROWSE_DIM + Plot.TOP);
			}
			// this does not happen for TorusPredPreyTasks because the
			// "Individual Info" panel is unnecessary, as panels for each
			// evolved agents are already shown with monitorInputs with all
			// of their sensors and information
			if (CommonConstants.monitorInputs) {
				Offspring.fillInputs((TWEANNGenotype) genotype);
			}
		}
		return new Pair<DrawingPanel, DrawingPanel>(panel, cppnPanel);
	}

}