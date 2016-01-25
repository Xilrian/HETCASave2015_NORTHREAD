/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hetca;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
class Parameters {

    public static int iMuta;
    public static int iDecay;
    public static int iSpawn;
    public static int iPuberty;
    public static int iExpec;
    public static int iInitial;
    public static int[] paraMuta = {1};
    public static int[] paraDecay = {1500};
    public static String[] densityAtInit = {"75%"};
    public static int[] paraExpec = {7};
    public static int[] paraPuberty = {0};
    public static String subfoldername;
    public static int numberOfFirstCycleInSlowMode = 20;
    public static int nGensBetweenStats = 2500;
    public static short ISNORMAL = 0;
    private static final byte ISVARIATION = 1;
    private static final byte ISSTABLE = 0;
    private static final byte ISSTRONG = 1;
    private static final byte ISNOVARIATION = 0;
    private static final byte ISLIGHT = 2;
    private static final byte ISSMAL = 3;

    private static final int[][][] propaChancesEvo = {
	{ 
	    //ISNOVARIATION,
	    {0, 1, 1, 1, 1, 1, 0, 0},
	},
	{
	    //ISSTRONG,
	    {0, 1, 1, 1, 1, 1, 0, 0},
	    {0, 0, 0, 1, 1, 1, 0, 0},
	    {0, 1, 1, 1, 0, 0, 0, 0},
	    {0, 0, 1, 0, 1, 1, 0, 0},
	    {0, 1, 0, 1, 1, 0, 0, 0},
	    {0, 0, 1, 1, 0, 1, 0, 0},
	    {0, 1, 1, 0, 1, 0, 0, 0},
	    {0, 1, 0, 1, 0, 1, 0, 0},
	    {0, 0, 1, 1, 1, 0, 0, 0},
	    {0, 1, 0, 0, 1, 1, 0, 0},
	    {0, 1, 1, 0, 0, 1, 0, 0},
	},
	{
	    //ISLIGHT
	    {0, 1, 1, 1, 1, 1, 0, 0},
	    {0, 1, 0, 1, 1, 1, 0, 0},
	    {0, 1, 1, 1, 1, 0, 0, 0},
	    {0, 0, 1, 1, 1, 1, 0, 0},
	    {0, 1, 1, 1, 0, 1, 0, 0},
	    {0, 1, 1, 0, 1, 1, 0, 0},
	},
	{
	    //ISSMALL,
	    {0, 0, 0, 1, 1, 1, 0, 0},
	    {0, 1, 1, 1, 0, 0, 0, 0},
	}
    };

    final static DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-hh-mm-ss");

    public static String RunName;

    public static Point UnivSize;

    static String Foldername;

    static int endingGeneration = 500000;
    static boolean processPatern = false;
    public static int iteratorSetting = 0;
    public static final String[] nameSetting = {
	"variation",
	"stable",
	"variationTrans",
	"variationLight",
	"variationSmall"

    };
    private static final byte[] isHeteroSetting = {
	ISVARIATION,
	ISSTABLE,
	ISVARIATION,
	ISVARIATION,
	ISVARIATION
    };

    private static final int[] transitionLengh = {
	1,
	0,
	60,
	1,
	1,
    };

    private static final byte[] propaChance = {
	ISSTRONG,
	ISNOVARIATION,
	ISSTRONG,
	ISLIGHT,
	ISSMAL,
    };

    private static final int[] propaCycle = {
	5000,
	0,
	5000,
	5000,
	100,
    };
    private static String[] autorisationsList;
    static boolean endSimu = false;
    static String endSimuCauses = "Unknown";
    private static int MaxRuns = 0;
    static boolean isCompare = false;
    static boolean isDensity = false;
    static String FolderDensity = "";

    static void setSubFolder() {
	subfoldername = nameSetting[iteratorSetting];
    }

    static void initParaneters(String FolderExport, int mutation) {
	try {
	    Foldername = new File(".").getCanonicalPath() + FolderExport;
	    System.out.println(Foldername);
	} catch (IOException ex) {
	    Logger.getLogger(Parameters.class.getName()).log(Level.SEVERE, null, ex);
	}
	LocalCell.percentageMutation = mutation;
	LocalCell.decay = Parameters.paraDecay[Parameters.iDecay];
	LocalCell.puberty = Parameters.paraPuberty[Parameters.iPuberty];

	Parameters.incrementSetting();
	Parameters.RunName = Parameters.setRunName();

    }

    static void incrementSetting() {
	int inc = 0;
	do {
	    inc++;
	    iteratorSetting = (iteratorSetting + 1) % nameSetting.length;
	} while (!isInAutorisationsList(nameSetting[iteratorSetting]) && inc <= nameSetting.length);
	if (inc > nameSetting.length) {
	    Parameters.endSimu = true;
	    Parameters.endSimuCauses = "All Configs In exceptions";
	}
	CAGird.simu++;
	setMaxSimu();
	if (CAGird.simu > MaxRuns) {
	    endSimuCauses = "All sims Done : " + CAGird.simu + "/" + MaxRuns;
	    endSimu = true;
	} else {
	    System.out.println("Processing run : " + CAGird.simu + "/" + MaxRuns);
	}
	
	setSubFolder();
    }

    static boolean isHetero() {
	return isHeteroSetting[Parameters.iteratorSetting] != Parameters.ISSTABLE;
    }

    static int getTransition() {
	return transitionLengh[Parameters.iteratorSetting];
    }

    static int getNewpropaChancesEvo(int i) {
	return propaChancesEvo[propaChance[Parameters.iteratorSetting]][((CAGird.Generation + 10) / propaCycle[Parameters.iteratorSetting]) % propaChancesEvo[propaChance[Parameters.iteratorSetting]].length][i];
    }

    static void checkTransition() {
	System.out.println("Cycle :" + CAGird.Generation + " - Transition: " + transitionLengh[Parameters.iteratorSetting] + " Phase transition: " + ((CAGird.Generation + 10) % 5000 + 1) + " - Mode: " + nameSetting[Parameters.iteratorSetting]);
    }

    static int propaInit(int i) {
	return propaChancesEvo[ISNOVARIATION][0][i];
    }

    static int getPreviouspropaChancesEvo(int i) {
	return propaChancesEvo[propaChance[Parameters.iteratorSetting]][((CAGird.Generation + 10) / propaCycle[Parameters.iteratorSetting] - 1) % propaChancesEvo[propaChance[Parameters.iteratorSetting]].length][i];
    }

    static String setRunName() {
	int i = 0;
	try {
	    while (allreadyUsedPath(Parameters.Foldername + "/" + Parameters.subfoldername + "/" + i + "-gnuplot.txt")) {
		i++;

	    }
	    File fichier = new File(Parameters.Foldername + "/" + Parameters.subfoldername + "/" + i + "-gnuplot.txt");
	    fichier.createNewFile();
	} catch (IOException ex) {
	    Logger.getLogger(Parameters.class.getName()).log(Level.SEVERE, null, ex);
	}
	return "" + i;
    }

    static boolean allreadyUsedPath(String path) throws IOException {
	File fichier = new File(path);
	return fichier.exists();
    }

    static boolean allreadyDone(String path, String fileName) throws IOException {
	File fichier = new File(path);
	return fichier.exists() || !isInAutorisationsList(fileName);
    }

    private static boolean isInAutorisationsList(String fileName) {
	try {
	    loadAutorisations();
	} catch (IOException ex) {
	    Logger.getLogger(Parameters.class.getName()).log(Level.SEVERE, null, ex);
	}
	boolean isInExceptionList = false;
	for (String exceptionList1 : autorisationsList) {
	    if (fileName.equals(exceptionList1)) {
		isInExceptionList = true;
	    }
	}
	return isInExceptionList;
    }

    private static void loadAutorisations() throws FileNotFoundException, IOException {
	FileInputStream ips = new FileInputStream("parameters/autorisations.txt");
	InputStreamReader ipsr = new InputStreamReader(ips);
	BufferedReader br = new BufferedReader(ipsr);
	String ligne;
	int linesNumber = 0;
	while ((ligne = br.readLine()) != null) {
	    linesNumber++;
	}
	ips = new FileInputStream("parameters/autorisations.txt");
	ipsr = new InputStreamReader(ips);
	br = new BufferedReader(ipsr);
	autorisationsList = new String[linesNumber];
	linesNumber = 0;
	while ((ligne = br.readLine()) != null) {
	    autorisationsList[linesNumber] = ligne;
	    linesNumber++;
	}

    }

    private static void setMaxSimu() {
	FileInputStream ips;
	try {
	    ips = new FileInputStream("parameters/maxSimus.txt");
	    InputStreamReader ipsr = new InputStreamReader(ips);
	    BufferedReader br = new BufferedReader(ipsr);
	    String ligne;
	    ligne = br.readLine();
	    MaxRuns = Integer.parseInt(ligne);
	} catch (FileNotFoundException ex) {
	    Logger.getLogger(Parameters.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IOException ex) {
	    Logger.getLogger(Parameters.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    static int getPropaCycle() {
	return propaCycle[Parameters.iteratorSetting];
    }

    static int getMinCycleUpPropa() {
	if(isCompare){
	    return 100;
	}
	else{
	    return 3000;
	}
    }

}
