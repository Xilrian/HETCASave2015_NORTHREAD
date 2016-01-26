/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hetca;

import static hetca.CAGird.Generation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author David
 */
class Statistics {

    public static String saveInfo = "";
    public static String saveGraph = "";
    public static int[] Population = new int[257];
    static int[] Populations = new int[257];
    static float[] PopVal = new float[10];
    static String addupgnu = "0 0 0 0 0 0 0 0";
    static String densityStats = "";
    static int nbrGenome = 0;
    static int maxPopGenome = 0;
    static int avragePopGenome = 0;
    static double[] allvariancespeloc = new double[20000000 / Parameters.nGensBetweenStats + 1];
    static int varianceiterator = 0;

    public static void GetMesures(double varvarspe) throws IOException {
	FileWriter lu = new FileWriter(Parameters.Foldername + "/" + Parameters.subfoldername + "/txt/" + Parameters.RunName + "/" + Generation + ".txt");

	BufferedWriter out = new BufferedWriter(lu);
	String allpopulations = "";
	String allpopulationsprime = "";
	int newline = 2;
	double totalpop = 0.0D;
	double maxpop = 0.0D;
	double minpop = Parameters.UnivSize.x * Parameters.UnivSize.y;

	for (int state = 0; state < LocalCell.getNumStates(); state++) {
	    double temppop = Populations[state];
	    if (state == States.DECAY) {
		temppop = Populations[state] + Populations[0];
	    }
	    if (newline == 2) {
		newline = 0;
		allpopulations = allpopulations + "\n";
	    } else {
		newline++;
	    }
	    allpopulations = allpopulations + "State " + Integer.toString(state) + ": " + Integer.toString(Populations[state]) + " | ";
	    allpopulationsprime = allpopulationsprime + " " + Integer.toString(Populations[state]);
	    if (state != States.QUIESCENT) {
		if (maxpop < temppop) {
		    maxpop = temppop;
		}
		totalpop += temppop;
		if (minpop > temppop) {
		    minpop = temppop;
		}
	    }

	}

	for (int e = 0; e < LocalCell.getNumStates(); e++) {
	    if (e == 0) {
		PopVal[e] = ((float) (Populations[e] + Populations[States.DECAY] - minpop) / (float) (maxpop - minpop));
	    } else {
		PopVal[e] = ((float) (Populations[e] - minpop) / (float) (maxpop - minpop));
	    } 

	}
	if (Generation > 1) {
	    if (((Generation >= Parameters.nGensBetweenStats) && (Generation <= Parameters.endingGeneration - Parameters.nGensBetweenStats)) || (Generation == Parameters.endingGeneration)) {
		addupgnu = getAllVar(Math.sqrt(varvarspe));
	    }

	    saveGraph = (Generation + " " + Population[Parameters.ISNORMAL] + allpopulationsprime + " " + Populations[States.AUTODECAY] + " " + Math.sqrt(varvarspe) + " " + addupgnu + " " + nbrGenome + " " + maxPopGenome + " " + Population[Parameters.ISNORMAL] / nbrGenome);

	    try {
		FileWriter gnuplot = new FileWriter(Parameters.Foldername + "/" + Parameters.subfoldername + "/" + Parameters.RunName + "-gnuplot.txt", true);
		BufferedWriter gnuout = new BufferedWriter(gnuplot);
		gnuout.write(saveGraph, 0, saveGraph.length());
		gnuout.newLine();
		gnuout.close();
	    } catch (Exception exc) {
	    }
	    out.write("Number of states " + LocalCell.getNumStates() + "\n Mutation : " + LocalCell.percentageMutation + "\n Spawning " + LocalCell.spawning + "\n Propagation " + LocalCell.propagation + "\n Puberty " + LocalCell.puberty + "\n Decay " + LocalCell.decay + "\n Total population : " + Population[Parameters.ISNORMAL] + allpopulations);

	    out.close();
	    LocalRule.DisplayTable(Parameters.Foldername + "/" + Parameters.subfoldername + "/txt/" + Parameters.RunName + "/tree_" + Generation, Parameters.Foldername + "/" + Parameters.subfoldername + "/bestGenomes/" + Parameters.RunName + "/" + Generation);

	    if (Generation > 15 && Parameters.processPatern) {

		SlidingMachine.Slide(CAGird.CACells, Parameters.UnivSize.x, Parameters.UnivSize.y, false);
		Patern.DisplayPatern(Parameters.Foldername + "/" + Parameters.subfoldername + "/txt/" + Parameters.RunName + "/patern_" + Generation);
		Patern.cleanAll();
	    }

	}
    }

    public static String getAllVar(double variancespeloc) {
	String ret = getVarFromConst(allvariancespeloc, variancespeloc) + "";

	varianceiterator++;
	return ret;
    }

    private static double getVarFromConst(double[] arraystat, double stat) {
	double sum = 0.0D;
	arraystat[varianceiterator] = stat;
	for (int i = 0; i <= varianceiterator; i++) {
	    sum += arraystat[i];
	}
	double avrage = sum / (varianceiterator + 1);
	sum = 0.0D;
	for (int i = 0; i <= varianceiterator; i++) {
	    sum += Math.pow(arraystat[i] - avrage, 2.0D);
	}

	return Math.sqrt(sum / (varianceiterator + 1));
    }

    static void deleteObsoDatas() throws IOException {
	File todel = new File(Parameters.Foldername + "/" + Parameters.subfoldername + "/image/" + Parameters.RunName);
	FileUtils.deleteDirectory(todel);
	todel = new File(Parameters.Foldername + "/" + Parameters.subfoldername + "/txt/" + Parameters.RunName);
	FileUtils.deleteDirectory(todel);
	todel = new File(Parameters.Foldername + "/" + Parameters.subfoldername + "/bestGenomes/" + Parameters.RunName);
	FileUtils.deleteDirectory(todel);
	File file = new File(Parameters.Foldername + "/" + Parameters.subfoldername + "/" + Parameters.RunName + "-gnuplot.txt");
	file.delete();

    }
    
    
    public static void consoleDisplay() {
	System.out.println("\nCycle: " + Integer.toString(CAGird.Generation));
    }

    static void processStatsDensity() {
	densityStats += CAGird.Generation+"\t"+Parameters.UnivSize.x+"\t"+Parameters.UnivSize.y+"\t"+Population[Parameters.ISNORMAL]+"\t"+DetectCycle.detectRecurence()+"\n";
//	System.out.println(Population[Parameters.ISNORMAL]);
	
    }

    static String getDensity() {
	return densityStats;
    }
}
