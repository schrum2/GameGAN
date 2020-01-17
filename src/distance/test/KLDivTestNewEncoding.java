/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distance.test;

import distance.convolution.ConvNTuple;
import distance.kl.KLDiv;
import distance.pattern.PatternCount;
import static distance.test.KLDivTest.getConvNTuple;
import static distance.util.MarioReader.readLevel;
import java.io.FileInputStream;

import static edu.southwestern.tasks.mario.level.LevelParser.tiles;

import java.util.List;
import java.util.Scanner;


/**
 *
 * @author developer
 */
public class KLDivTestNewEncoding {
    // no concern about where they come from for now
    // the principle is the same
    static String inputFile1 = "data/mario/levelsNewEncodingTest/mario-1-1.txt";
    static String inputFile2 = "data/mario/levelsNewEncodingTest/mario-8-1.txt";

    static String inputFile3 = "data/mario/levelsNewEncodingTest/mario-5-1.txt";
    
    static int filterWidth = 5;
    static int filterHeight = 10;
    static int stride = 1;
    
    public static void main(String[] args) throws Exception {
        int[][] level1 = readLevel(new Scanner(new FileInputStream(inputFile1)), tiles);
        int[][] level2 = readLevel(new Scanner(new FileInputStream(inputFile2)), tiles);
        int[][] level3 = readLevel(new Scanner(new FileInputStream(inputFile3)), tiles);
        
        ConvNTuple c1 = getConvNTuple(level1, filterWidth, filterHeight, stride);
        ConvNTuple c2 = getConvNTuple(level2, filterWidth, filterHeight, stride);
        ConvNTuple c3 = getConvNTuple(level3, filterWidth, filterHeight, stride);
        
        double klDiv12 = KLDiv.klDiv(c1.sampleDis, c2.sampleDis);
        double klDiv21 = KLDiv.klDiv(c2.sampleDis, c1.sampleDis);
        double klDiv = KLDiv.klDivSymmetric(c1.sampleDis, c2.sampleDis);

        System.out.println("klDiv 1-2: " + klDiv12);
        System.out.println("klDiv 2-1: " + klDiv21);
        System.out.println("klDiv Sym: " + klDiv);

        System.out.println("Adding a distribution to itself");
        c1.sampleDis.add(c1.sampleDis);
        System.out.println("Sanity check, should be same as before: KLDiv Sym 1-2: " + KLDiv.klDivSymmetric(c1.sampleDis, c2.sampleDis));

        System.out.println("Adding c3 to c1");
        c1.sampleDis.add(c3.sampleDis);
        System.out.println("Sanity check, should be different KLDiv Sym 1-2: " + KLDiv.klDivSymmetric(c1.sampleDis, c2.sampleDis));

        System.out.println();
        System.out.println("Sanity check, should be zero: KLDiv Sym 1-1: " + KLDiv.klDiv(c1.sampleDis, c1.sampleDis));
        
    }

}
