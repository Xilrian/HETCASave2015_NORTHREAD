package hetca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

//-----------------------------------------------------------------------
public class ProcessSizeAndPop {
    public static double maxError = 0.05;

    // ----------------------------------------------------------------
    // Default constructor, some VMs require it
    public static void main(String[] args) {
	for (int i = 0; i < ParametersExport.params.length; i++) {
	    System.out.println(ParametersExport.params[i] + ":\n\n");
	    String pathFolder = "/Volumes/1THD/valid/" + ParametersExport.params[i] + "/txt/";
	    processParam(pathFolder, i);
	    pathFolder = "/Volumes/1THD/valid/" + ParametersExport.params[i] + "/";
	    processParamGnuFile(pathFolder, i);
	    System.out.println("\n\n\n\n");
	}

    }
    // ----------------------------------------------------------------
    // The main initialization

    private static String[][] FileToArray(String path, String separator, boolean takemin) {
	String[][] data = null;
	File file = new File(path);
	if (file.exists() && file.getTotalSpace() > 0) {
	    try {
		FileInputStream ips = new FileInputStream(path);
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		int curLine = 0;
		String ligne;

		int numColonnes = 0;
		int numLignes = 0;
		while ((ligne = br.readLine()) != null) {
		    if (takemin) {

			if (ligne.split(separator).length >= ParametersExport.columnToParse2[ParametersExport.columnToParseName2.length - 1]) {
			    numLignes++;

			    if (numColonnes == 0 || numColonnes > ligne.split(separator).length) {
				numColonnes = ligne.split(separator).length;
			    }
			}
		    } else {
			if (ligne.split(separator).length > numColonnes) {
			    numColonnes = ligne.split(separator).length;
			    numLignes = 1;
			} else if (ligne.split(separator).length == numColonnes) {
			    numLignes++;
			}
		    }
		}
		br.close();

		data = new String[numLignes][numColonnes];
		int ligneNumber = 0;
		ips = new FileInputStream(path);
		ipsr = new InputStreamReader(ips);
		br = new BufferedReader(ipsr);
		while ((ligne = br.readLine()) != null) {
		    if (ligne.split(separator).length >= numColonnes) {
			String[] toto = ligne.split(separator);
			System.arraycopy(toto, 0, data[ligneNumber], 0, numColonnes);
			ligneNumber++;
		    }
		}
		br.close();
	    } catch (Exception e) {
		System.out.println(e.toString());
	    }
	} else {
	    System.out.print(path + " do not exists");
	}
	return data;
    }

    private static void display(String[][] data, String separator) {
	for (String[] data1 : data) {
	    for (String data11 : data1) {
		System.out.print(data11 + separator);
	    }
	    System.out.print("\n");
	}
    }

    private static void displayWithI(String[][] data, String separator) {
	for (String[] data1 : data) {
	    for (int i = 0; i < data1.length; i++) {
		System.out.print(i + ":" + data1[i] + separator);
	    }
	    System.out.print("\n");
	}
    }

    private static double[][] parseDouble(String[][] data, int[] columnDouble) {
	double[][] dataDouble = new double[columnDouble.length][data.length];
	for (int i = 0; i < data.length; i++) {
	    for (int j = 0; j < columnDouble.length; j++) {
		dataDouble[j][i] = Double.parseDouble(cleanDouble(data[i][columnDouble[j]]));
	    }
	}
	return dataDouble;
    }

    private static double[][] parseDoubleNormal(String[][] data, int[] columnDouble) {
	double[][] dataDouble = new double[data.length][columnDouble.length];
	for (int i = 0; i < data.length; i++) {
	    for (int j = 0; j < columnDouble.length; j++) {
		dataDouble[i][j] = Double.parseDouble(cleanDouble(data[i][columnDouble[j]]));
	    }
	}
	return dataDouble;
    }

    private static String cleanDouble(String data) {
	return data.replaceAll(",", ".").replaceAll("[^0-9.]", "");
    }

    private static void display(double[][] data, String separator) {

	for (double[] data1 : data) {
	    displaySimple(data1, separator);
	    System.out.print("\n");
	}
    }

    private static double[] ComputeMedian(double[][] dataDouble) {
	double[] dataMedian = new double[dataDouble.length];
	for (int i = 0; i < dataMedian.length; i++) {
	    dataMedian[i] = median(dataDouble[i]);
	}
	return dataMedian;
    }

    private static void ComputeMedian(double[][] dataDouble, double[][][] medianExport, int j, int idrun) {
	for (int i = 0; i < dataDouble.length; i++) {
	    medianExport[j][i][idrun] = median(dataDouble[i]);
	}
	medianExport[j][dataDouble.length][idrun] = dataDouble[0].length;
	medianExport[j][dataDouble.length + 1][idrun] = sum(dataDouble[0]);
    }

    private static double[] ComputeAverage(double[][] dataDouble) {
	double[] dataMedian = new double[dataDouble.length];
	for (int i = 0; i < dataMedian.length; i++) {
	    dataMedian[i] = average(dataDouble[i]);
	}
	return dataMedian;
    }

    private static void ComputeAverage(double[][] dataDouble, double[][][] averageExport, int j, int idrun) {
	for (int i = 0; i < dataDouble.length; i++) {
	    averageExport[j][i][idrun] = average(dataDouble[i]);
	}
	averageExport[j][dataDouble.length][idrun] = dataDouble[0].length;
	averageExport[j][dataDouble.length + 1][idrun] = sum(dataDouble[0]);
    }

    private static double median(double[] dataDouble) {

	double[] dataCopy = dataDouble.clone();
	Arrays.sort(dataCopy);
	double median;
	if (dataCopy.length % 2 == 0) {
	    median = ((double) dataCopy[dataCopy.length / 2] + (double) dataCopy[dataCopy.length / 2 - 1]) / 2;
	} else {
	    median = (double) dataCopy[dataCopy.length / 2];
	}
	return median;
    }

    private static double average(double[] dataDouble) {
	return sum(dataDouble) / dataDouble.length;
    }

    private static double std(double[] averageExport, double averageOfaverageExport) {
	return Math.sqrt(partstd(averageExport, averageOfaverageExport) / (averageExport.length-1))/Math.sqrt(averageExport.length);

    }

    private static void displaySimple(double[] data, String separator) {
	for (double data11 : data) {
	    System.out.print(data11 + separator);
	}
    }

    private static void displaySimple(String[] data, String separator) {

	for (String data11 : data) {
	    System.out.print(data11 + separator);
	}
    }

    private static String arrayToString(double[] data, String separator) {
	String ret = "";
	boolean first = true;
	for (double data11 : data) {
	    if (!first) {
		ret += separator;
	    } else {
		first = false;
	    }
	    ret += data11;
	}
	return ret;
    }

    private static String arrayToString(double[][][] export, int i, int k, String separator) {
	String ret = "";
	boolean first = true;
	for (double[][] data : export) {
	    if (!first) {
		ret += separator;
	    } else {
		first = false;
	    }
	    ret += data[i][k];
	}
	return ret;
    }

    private static void processTreeAtGeneration(String pathFolder, int i, double[][][] averageExport, double[][][] medianExport, int iteration, int idrun) {
	String path = pathFolder + "tree_" + i + ".txt";
	String[][] data = FileToArray(path, " ", false);
	double[][] dataDouble = parseDouble(data, ParametersExport.columnToParse);
	ComputeMedian(dataDouble, medianExport, iteration, idrun);
	ComputeAverage(dataDouble, averageExport, iteration, idrun);
    }

    private static void parseFolder(String pathFolder, double[][][] averageExport, double[][][] medianExport, int idrun) {

	for (int i = 2500; i < ParametersExport.maxIteration; i = i + ParametersExport.stepIteration) {
	    processTreeAtGeneration(pathFolder + idrun + "/", i, averageExport, medianExport, i / ParametersExport.stepIteration - 1, idrun);
	}
	processTreeAtGeneration(pathFolder + idrun + "/", 499999, averageExport, medianExport, ParametersExport.maxIteration / ParametersExport.stepIteration - 1, idrun);

    }

    private static void displaySimple3Dim(double[][] data, String separator, int idrun) {
	for (double[] data11 : data) {
	    System.out.print(data11[idrun] + separator);
	}
    }

    private static void display3Dim(double[][][] data, String separator, int idrun) {

	for (double[][] data1 : data) {
	    displaySimple3Dim(data1, separator, idrun);
	    System.out.print("\n");
	}
    }

    private static void processMedianArray(double[][][] medianOfaverageExport, double[][][] averageExport) {
	for (int i = 0; i < medianOfaverageExport.length; i++) {
	    for (int j = 0; j < medianOfaverageExport[i].length; j++) {
		medianOfaverageExport[i][j][0] = median(averageExport[i][j]);
		double [] errors =    
				ProcessStatsGeneric.getErrorMedian(averageExport[i][j],maxError);
		medianOfaverageExport[i][j][1] = errors[0];
		medianOfaverageExport[i][j][2] = errors[1];
	    }
	}
    }

    private static void processAverageArray(double[][][] averageOfaverageExport, double[][][] averageExport) {
	for (int i = 0; i < averageOfaverageExport.length; i++) {
	    for (int j = 0; j < averageOfaverageExport[i].length; j++) {
		averageOfaverageExport[i][j][0] = average(averageExport[i][j]);
		averageOfaverageExport[i][j][1] = std(averageExport[i][j], averageOfaverageExport[i][j][0]);
	    }
	}
    }

    private static double sum(double[] dataDouble) {
	double sum = 0;
	for (double d : dataDouble) {
	    sum += d;
	}
	return sum;
    }

    private static double partstd(double[] averageExport, double average) {

	double sum = 0;
	for (int i = 0; i < averageExport.length; i++) {
	    sum += Math.pow(averageExport[i] - average, 2);
	}
	return sum;
    }

    private static void processParam(String pathFolder, int param) {

	double[][][] averageExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName.length][ParametersExport.nbrRun[param]];
	double[][][] medianExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName.length][ParametersExport.nbrRun[param]];
	for (int i = 0; i < ParametersExport.nbrRun[param]; i++) {
	    parseFolder(pathFolder, averageExport, medianExport, i);
	}

	double[][][] medianOfaverageExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName.length][3];
	double[][][] medianOfmedianExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName.length][3];
	double[][][] averageOfaverageExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName.length][2];
	double[][][] averageOfmedianExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName.length][2];
	//double[][] stdOfaverageExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName.length];
	//double[][] stdOfavgmedExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName.length];

	exportBoxPlot(averageExport, "boxOfavg", ParametersExport.params[param], ParametersExport.columnToParseName);
	exportBoxPlot(medianExport, "boxOfmed", ParametersExport.params[param], ParametersExport.columnToParseName);

	processMedianArray(medianOfaverageExport, averageExport);
	processMedianArray(medianOfmedianExport, medianExport);
	processAverageArray(averageOfaverageExport, averageExport);
	processAverageArray(averageOfmedianExport, medianExport);
	

	exportResults(medianOfaverageExport, "medOfavg", ParametersExport.params[param], ParametersExport.columnToParseName);
	exportResults(medianOfmedianExport, "medOfmed", ParametersExport.params[param], ParametersExport.columnToParseName);
	exportResults(averageOfaverageExport, "avgOfavg", ParametersExport.params[param], ParametersExport.columnToParseName);
	exportResults(averageOfmedianExport, "avgOfmed", ParametersExport.params[param], ParametersExport.columnToParseName);

	System.out.println("End Export P");
    }

    private static void exportResults(double[][][] medianOfaverageExport, String mode, String param, String[] dataName) {
	for (int i = 0; i < dataName.length; i++) {
	    exportNormal(medianOfaverageExport, i, mode, param, dataName);
	}
    }

    private static void exportBoxPlot(double[][][] export, String mode, String param, String[] dataName) {
	for (int i = 0; i < dataName.length; i++) {
	    export(export, i, mode, param, dataName);
	}
    }

    private static void exportNormal(double[][][] medianOfaverageExport, int i, String mode, String param, String[] dataName) {
	File fichier = new File(ParametersExport.Foldername + dataName[i] + "/" + param + "-" + mode + ".txt");
	fichier.getParentFile().mkdirs();

	try {
	    fichier.createNewFile();

	    PrintWriter writer = new PrintWriter(fichier, "UTF-8");

	    for (double[][] medianOfaverageExport1 : medianOfaverageExport) {
		writer.println(arrayToString(medianOfaverageExport1[i],";"));
	    }
	    writer.close();
	} catch (IOException ex) {
	    Logger.getLogger(ProcessSizeAndPop.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    private static void export(double[][][] export, int i, String mode, String param, String[] dataName) {
	File fichier = new File(ParametersExport.Foldername + dataName[i] + "/" + param + "-" + mode + ".txt");
	fichier.getParentFile().mkdirs();

	try {
	    fichier.createNewFile();

	    PrintWriter writer = new PrintWriter(fichier, "UTF-8");

	    for (int k = 0; k < export[0][0].length; k++) {
		writer.println(arrayToString(export, i, k, ";"));
	    }
	    writer.close();
	} catch (IOException ex) {
	    Logger.getLogger(ProcessSizeAndPop.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private static void processParamGnuFile(String pathFolder, int param) {

	double[][][] export = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName2.length][ParametersExport.nbrRun[param]];
	for (int idRun = 0; idRun < ParametersExport.nbrRun[param]; idRun++) {
	    parseGnu(pathFolder + "/" + idRun + "-gnuplot.txt", export, idRun);
	}

	double[][][] medianOfExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName2.length][3];
	double[][][] averageOfExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName2.length][2];
//	double[][] stdOfExport = new double[ParametersExport.maxIteration / ParametersExport.stepIteration][ParametersExport.columnToParseName2.length];

	exportBoxPlot(export, "boxOfavg", ParametersExport.params[param], ParametersExport.columnToParseName2);

	processMedianArray(medianOfExport, export);
	processAverageArray(averageOfExport, export);
	

	System.out.println("Start Export");
	exportResults(medianOfExport, "medOfmed", ParametersExport.params[param], ParametersExport.columnToParseName2);
	exportResults(averageOfExport, "avgOfavg", ParametersExport.params[param], ParametersExport.columnToParseName2);
//	exportResults(stdOfExport, "stdOfavg", ParametersExport.params[param],ParametersExport.columnToParseName2);
	System.out.println("Done Export");

    }

    private static void parseGnu(String path, double[][][] export, int idRun) {
	String[][] data = FileToArray(path, " ", true);
	double[][] dataDouble = parseDoubleNormal(data, ParametersExport.columnToParse2);
	for (double[] dataDouble1 : dataDouble) {
	    if (((int) dataDouble1[0]) % ParametersExport.stepIteration == 0) {
		for (int j = 0; j < ParametersExport.columnToParseName2.length; j++) {
		    export[((int) dataDouble1[0]) / ParametersExport.stepIteration - 1][j][idRun] = dataDouble1[j];
		}
	    }
	}
	for (int j = 0; j < ParametersExport.columnToParseName2.length; j++) {
	    export[export.length - 1][j][idRun] = dataDouble[dataDouble.length - 1][j];
	}
    }

    private static void display3D(double[][][] export, String separator) {
	for (int run = 0; run < export[0][0].length; run++) {
	    System.out.println("\n\n\n\nRun:" + run + ":\n");
	    display3Dim(export, separator, run);
	}
    }

}
