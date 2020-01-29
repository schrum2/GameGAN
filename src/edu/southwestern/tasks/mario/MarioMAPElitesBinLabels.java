package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

public class MarioMAPElitesBinLabels implements BinLabels {
	
	public static final int BINS_PER_DIMENSION = 10;
	
	List<String> labels = null;
		
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded
			
			// This code is from Zelda. Need to make specific to Mario
			
//			labels = new ArrayList<String>(size);
//			for(int i = 0; i < TILE_GROUPS; i++) { // Wall tile percent
//				for(int j = 0; j < TILE_GROUPS; j++) { // Water tile percent
//					for(int r = 0; r <= maxNumRooms; r++) {
//						labels.add("Wall["+i+"0-"+(i+1)+"0]Water["+j+"0-"+(j+1)+"0]Rooms"+r);
//					}
//				}
//			}
		}
		return labels;
	}

}