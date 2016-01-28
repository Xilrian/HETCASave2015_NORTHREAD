/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hetca;

/**
 *
 * @author David
 */
public class ParametersExport {
    

    static int[] columnToParse = {9, 18, 24, 28, 34};
    static int[] columnToParse2 = {0, 2, 8};
    static String[] columnToParseName = {"Frequence", "Creation", "MutaStep", "Size Genotype", "Activity", "pop genotype", "pop celules"};
    static String[] columnToParseName2 = {"Generation","Quiescent","Decay"};

    public static final int maxIteration = 500000;
    public static final int stepIteration = 2500;
    public static final int [] nbrRun  = {51,24,24,51,37};
    static String[] params = {"varSmallValid", "varTransValid", "varLightValid", "StableValid", "varValid"};
    static int stepIterationGenotype = 100000;
    static String Foldername = "Results/";
    static int densityEndingGeneration = 60000;
    
}
