package hetca;

import ca.kowaliw.gp.GPUtils;
import static hetca.PaletteDeCouleurs.ActivatePalette;
import static hetca.Parameters.Foldername;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

final class CAGird {

    public static final int MAX_CLO = 255;
    static int simu = 0;

    private static void cycle() {
	while (!cycleIsOver()) {
	    if (Parameters.isHetero()) {
		States.updatePropagationChances();
	    }

	    if (Generation == 0) {
		Statistics.Population[Parameters.ISNORMAL] = 500000;
	    } else {
		if(Parameters.isDensity){
			Cycle.OneGenerationDensity();
		    
		}
		else{
			Cycle.OneGeneration();
		}
	    }
	    Statistics.processStatsDensity();

	    if (isGenerationRefreshed()) {
		if(Parameters.isDensity){
		    try {
			saveImageFile();
		    } catch (IOException ex) {
			Logger.getLogger(CAGird.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}
		else{
		    Statistics.consoleDisplay();
		    processDatas();
		}
	    }

	    Generation++;
	}
    }

    private static void RandomizeGrid(String sHow) {

	double maxVal;
	sHow = sHow.substring(0, sHow.length() - 1);
	int iff = Integer.parseInt(sHow.trim());
	maxVal = iff / 100.0D;
	for (int xloop = 0; xloop < Parameters.UnivSize.x; xloop++) {
	    for (int yloop = 0; yloop < Parameters.UnivSize.y; yloop++) {
		RandomizeOneCell(xloop, yloop, maxVal);
		CAGird.CAStatesTmp[xloop][yloop] = 0;
	    }

	}
    }

    private final boolean InitDone = false;
    public static int RefreshStep = 1;
    static int Generation;
    public static int StatesCount = 2;

    public static LocalCell[][] CACells;
    public static byte[][] CAStatesTmp;

    static void CAGirdInit(int width, int height) {
	if (Parameters.processPatern) {
	    Patern.initiate();
	    Patern.initiateS();
	}
	if(!Parameters.isDensity){
	    Parameters.initParaneters("/BeforeExport4",Parameters.paraMuta[Parameters.iMuta]);
	}
	initLGP();
	initCA(width, height);
	SetStatesCount(9);
    }

    public static void run() {
	Generation = 0;
	Randomize(Parameters.densityAtInit[Parameters.iInitial]);
	while (!Parameters.endSimu) {
	    cycle();
	    saveStatsAndReinit();
	}
	System.out.println(Parameters.endSimuCauses);
    }

    public static void runCompare() {
	Generation = 0;
	
	Randomize(Parameters.densityAtInit[Parameters.iInitial]);
	while (!Parameters.endSimu) {
	    cycle();
	    saveStatsAndReinitCompare();
	}
	System.out.println(Parameters.endSimuCauses);
    }
    
    

    static String runDensity(String pathGenotype, String uniqueid) {
	Generation = 0;
	LocalRule.initialize(pathGenotype);
	RandomizeGrid(Parameters.densityAtInit[Parameters.iInitial]);
	cycle();
	return Statistics.getDensity();
    }

    public static void processDatas() {

	try {
	    saveFiles();
	    Statistics.GetMesures(printVarSpe());

	} catch (IOException ex) {
	    Logger.getLogger(CAGird.class
		    .getName()).log(Level.SEVERE, null, ex);
	}
    }

    public static void RandomizeOneCell(int x, int y, double maxVal) {
	if ((Math.random() <= maxVal)) {
	    byte newStt = (byte) (int) Math.ceil(Math.random() * (CAGird.StatesCount - 1));
	    CACells[x][y].randomSetAll(newStt, Parameters.ISNORMAL);
	} else {
	    CACells[x][y].randomSetAll((byte) 0, Parameters.ISNORMAL);
	}
    }

    public static void Randomize(String sHow) {
	

	LocalRule.initialize();
	if (Parameters.processPatern) {
	    Patern.initialize();
	}
	
	RandomizeGrid(sHow);
    }

    public static void SetStatesCount(int iSttCnt) {
	CAGird.StatesCount = iSttCnt;

	ActivatePalette(PaletteDeCouleurs.PalName, CAGird.StatesCount);
    }

    private static void saveFiles() throws IOException {
	saveImageFile();
	File rep2 = new File(Parameters.Foldername + "/" + Parameters.subfoldername + "/txt/" + Parameters.RunName);

	if (!rep2.exists()) {
	    rep2.mkdirs();
	}
	File rep3 = new File(Parameters.Foldername + "/" + Parameters.subfoldername + "/bestGenomes/" + Parameters.RunName);

	if (!rep3.exists()) {
	    rep3.mkdirs();
	}
    }

    private static void saveImageFile() throws IOException {
	BufferedImage theImage = new BufferedImage(Parameters.UnivSize.x, Parameters.UnivSize.y, BufferedImage.TYPE_INT_RGB);
	int point;

	for (int y = 0; y < Parameters.UnivSize.y; y++) {
	    for (int x = 0; x < Parameters.UnivSize.x; x++) {
		point = PaletteDeCouleurs.Palette[CACells[x][y].GetStateWthAudoDecay()];
		theImage.setRGB(x, y, point);
	    }
	}
	if(Parameters.isDensity){
	    		BufferedImageBuilder.imageToBufferedImageSave2(theImage, Parameters.FolderDensity, Generation);

	}
	else{
	    		BufferedImageBuilder.imageToBufferedImageSave2(theImage, Parameters.Foldername + "/" + Parameters.subfoldername + "/image/" + Parameters.RunName, Generation);
	    
	}

    }

    private static double printVarSpe() {
	double totalvariancespe = 0.0D;
	short[] allstates = new short[Parameters.UnivSize.y * Parameters.UnivSize.x];

	int ff = 0;
	for (int x = 0; x < Parameters.UnivSize.x; x++) {
	    for (int y = 0; y < Parameters.UnivSize.y; y++) {
		allstates[ff] = ((short) CACells[x][y].GetStateVarPrime());
		totalvariancespe += allstates[ff];
		ff++;
	    }
	}
	totalvariancespe /= Parameters.UnivSize.x * Parameters.UnivSize.y;
	double varianceofvariancespe = 0.0D;
	for (int f = 0; f < allstates.length; f++) {
	    varianceofvariancespe += Math.pow(allstates[f] - totalvariancespe, 2.0D);
	}
	varianceofvariancespe /= Parameters.UnivSize.x * Parameters.UnivSize.y;
	return varianceofvariancespe;
    }

    private static void initLGP() {
	LocalRule.numAdditionalRegisters = LocalCell.getNumStates() + 2;
	ca.kowaliw.gp.GPParams.maxInitLength = 50;
	ca.kowaliw.gp.GPParams.maxLength = 50;
	LocalRule.initialize();
	GPUtils.zeroAllOps();
	GPUtils.addOp(GPUtils.ABS);
	GPUtils.addOp(GPUtils.ADD);
	GPUtils.addOp(GPUtils.DELTA);
	GPUtils.addOp(GPUtils.DISTANCE);
	GPUtils.addOp(GPUtils.INV);
	GPUtils.addOp(GPUtils.INV2);
	GPUtils.addOp(GPUtils.MAGPLUS);
	GPUtils.addOp(GPUtils.MAX);
	GPUtils.addOp(GPUtils.MIN);
	GPUtils.addOp(GPUtils.SAFEDIV);
	GPUtils.addOp(GPUtils.SAFEPOW);
	GPUtils.addOp(GPUtils.THRESH);
	GPUtils.addOp(GPUtils.TIMES);
	GPUtils.addOp(GPUtils.ISZERO);
	GPUtils.init();
    }

    public static void initCA(int sizX, int sizY) {
	System.out.println(sizY+"//"+sizX);
	Parameters.UnivSize = new Point(sizX, sizY);

	Generation = 0;

	CACells = new LocalCell[Parameters.UnivSize.x][Parameters.UnivSize.y];
	CAGird.CAStatesTmp = new byte[Parameters.UnivSize.x][Parameters.UnivSize.y];
	Cycle.CAStatesTmpDeBug = new byte[Parameters.UnivSize.x][Parameters.UnivSize.y];
	Cycle.CAStatesTmpDeBug2 = new String[Parameters.UnivSize.x][Parameters.UnivSize.y];
	for (short xloop = 0; xloop < Parameters.UnivSize.x; xloop++) {
	    for (short yloop = 0; yloop < Parameters.UnivSize.y; yloop++) {
		CACells[xloop][yloop] = new LocalCell(xloop, yloop, true);
		CAGird.CAStatesTmp[xloop][yloop] = 0;
		Cycle.CAStatesTmpDeBug[xloop][yloop] = -10;
		Cycle.CAStatesTmpDeBug2[xloop][yloop] = "";
	    }
	}
    }

    private static boolean cycleIsOver() {
	return (Generation != 0) && ((Statistics.Population[Parameters.ISNORMAL] == 0) || (Generation == Parameters.endingGeneration));
    }

    private static void saveStatsAndReinit() {
	if (CAGird.Generation < 10000) {

	    System.out.println("DELETE");
	    try {
		Statistics.deleteObsoDatas();
	    } catch (IOException ex) {
		Logger.getLogger(CAGird.class.getName()).log(Level.SEVERE, null, ex);
	    }
	} else {
	    try {
		FileWriter gnuplot = new FileWriter(Parameters.Foldername + "/" + Parameters.subfoldername + "/" + Parameters.RunName + "-gnuplot.txt", true);
		BufferedWriter gnuout = new BufferedWriter(gnuplot);
		gnuout.write("Ending Cycle : " + CAGird.Generation);

	    } catch (IOException ex) {
		Logger.getLogger(CAGird.class
			.getName()).log(Level.SEVERE, null, ex);
	    }
	    Statistics.saveInfo = (Statistics.saveInfo + "Subname : " + Parameters.RunName + " Last cycle : " + Generation + " Population : " + Statistics.Population[Parameters.ISNORMAL] + "\n");

	    try {
		FileWriter info = new FileWriter(Foldername + "/" + Parameters.subfoldername + "/info.txt");
		BufferedWriter infout = new BufferedWriter(info);
		infout.write(Statistics.saveInfo);

	    } catch (IOException ex) {
		Logger.getLogger(CAGird.class.getName()).log(Level.SEVERE, null, ex);
	    }
	    Parameters.incrementSetting();

	}

	CAGird.RefreshStep = 1;
	Date actuelle = new Date();
	Parameters.RunName = Parameters.setRunName();
//		    Parameters.dateFormat.format(actuelle);
	Statistics.saveGraph = "";
	Randomize(Parameters.densityAtInit[Parameters.iInitial]);
	Generation = 0;
	States.reinitPropa();
	Statistics.varianceiterator = 0;
	Statistics.saveInfo = "";
    }

    private static void saveStatsAndReinitCompare() {
	if (CAGird.Generation < 10000) {

	    System.out.println("DELETE");
	    try {
		Statistics.deleteObsoDatas();
	    } catch (IOException ex) {
		Logger.getLogger(CAGird.class.getName()).log(Level.SEVERE, null, ex);
	    }
	} else {
	    try {
		FileWriter gnuplot = new FileWriter(Parameters.Foldername + "/" + Parameters.subfoldername + "/" + Parameters.RunName + "-gnuplot.txt", true);
		BufferedWriter gnuout = new BufferedWriter(gnuplot);
		gnuout.write("Ending Cycle : " + CAGird.Generation);

	    } catch (IOException ex) {
		Logger.getLogger(CAGird.class
			.getName()).log(Level.SEVERE, null, ex);
	    }
	    Statistics.saveInfo = (Statistics.saveInfo + "Subname : " + Parameters.RunName + " Last cycle : " + Generation + " Population : " + Statistics.Population[Parameters.ISNORMAL] + "\n");

	    try {
		FileWriter info = new FileWriter(Foldername + "/" + Parameters.subfoldername + "/info.txt");
		BufferedWriter infout = new BufferedWriter(info);
		infout.write(Statistics.saveInfo);

	    } catch (IOException ex) {
		Logger.getLogger(CAGird.class.getName()).log(Level.SEVERE, null, ex);
	    }
	    Parameters.incrementSetting();

	}

	CAGird.RefreshStep = 1;
	Date actuelle = new Date();
	Parameters.RunName = Parameters.setRunName();
//		    Parameters.dateFormat.format(actuelle);
	Statistics.saveGraph = "";
	
	Randomize(Parameters.densityAtInit[Parameters.iInitial]);
	Generation = 0;
	States.reinitPropa();
	Statistics.varianceiterator = 0;
	Statistics.saveInfo = "";
    }

    private static boolean isGenerationRefreshed() {
	boolean doRedraw = false;
	if (Generation == Parameters.numberOfFirstCycleInSlowMode) {
	    CAGird.RefreshStep = Parameters.nGensBetweenStats;
	} else if ((Generation == Parameters.endingGeneration - Parameters.numberOfFirstCycleInSlowMode) || (Generation == 0) || (Generation == 1)) {
	    CAGird.RefreshStep = 1;
	}

	if (Generation % CAGird.RefreshStep == 0 || Generation == 100) {
	    doRedraw = true;
	}

	return doRedraw;
    }
}
