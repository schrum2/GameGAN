/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distance.test;

import gvgai.core.vgdl.VGDLFactory;
import gvgai.core.vgdl.VGDLRegistry;

/**
 *
 * @author developer
 */
public class InteractiveZeldaKLDivAnalysis {
    static String trainingInputPath = "data/mario/levelsNewEncodingTest/";
    static String interactiveInputPath= "data/mario/MarioInteractiveLast/";
    static String initialVectors = "data/mario/initialVectors.txt";
    static String logPath = "data/mario/";
    
    static int filterWidth = 5;
    static int filterHeight = 5;
    static int stride = 1;
    
    public static void main(String[] args) throws Exception {
        VGDLFactory.GetInstance().init();
        VGDLRegistry.GetInstance().init();
    }

}
