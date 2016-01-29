package hetca;

import static hetca.Parameters.allreadyUsedPath;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

//-----------------------------------------------------------------------
public class ProcessDensity {

    // ----------------------------------------------------------------
    // Default constructor, some VMs require it
    public static void main(String[] args) {
	
	Parameters.nGensBetweenStats = 300;
	for (int iparam = 0; iparam < ParametersExport.params.length; iparam++) {
	    System.out.println(ParametersExport.params[iparam] + ":\n\n");
	    String pathFolder = "valid/" + ParametersExport.params[iparam] + "/bestGenomes/";
	    processParam(pathFolder, iparam);
	}
    }
    
    
    // ----------------------------------------------------------------
    // The main initialization
    private static void processParam(String pathFolder, int param) {

	for (int run = 0; run < ParametersExport.nbrRun[param]; run++) {
	    processRun(pathFolder, param, run);
	}
    }

    private static void processRun(String pathFolder, int param, int run) {
	for (int generation = 2500; generation < ParametersExport.maxIteration; generation = generation + ParametersExport.stepIterationGenotype) {
	    processGenotype(pathFolder, param, run, generation);
	}
	processGenotype(pathFolder, param, run, 499999);
    }

    private static void processGenotype(String pathFolder, int param, int run, int generation) {
	try {
	    if(allreadyUsedPath(pathFolder + "/" + run + "/" + generation + "-1.txt")){
		for (Parameters.iteratorSetting = 0; Parameters.iteratorSetting < Parameters.nameSetting.length; Parameters.iteratorSetting++) {
		    if(Parameters.iteratorSetting!=2){
			System.out.println("\n\n!!!!!!!!!!"+Parameters.nameSetting[Parameters.iteratorSetting]+"!!!!!!!!!!\n\n");
			String path = "predensity" + ParametersExport.Foldername + "/" + ParametersExport.params[param] + "/" + generation + "/" +  Parameters.nameSetting[Parameters.iteratorSetting]+"/"+run+"/";
			String pathDensity = "predensity" + ParametersExport.Foldername + ParametersExport.params[param] + "-" + generation + "-" +  Parameters.nameSetting[Parameters.iteratorSetting]+"-"+run;
			String uniqueid = ParametersExport.params[param] + "-" + generation + "-" + run;
			try {
			    if (!allreadyUsedPath(pathDensity+".txt")) {
				System.out.println(pathDensity+".txt");
				File fichier = new File(pathDensity+".txt");
				fichier.getParentFile().mkdirs();
				fichier.createNewFile();
				
				String density = processDensityGenotype(pathFolder + "/" + run + "/" + generation + "-1.txt", uniqueid,path);
				PrintWriter writer = new PrintWriter(fichier, "UTF-8");
				
				writer.println(density);
				writer.close();
				
			    }
			} catch (IOException ex) {
			    Logger.getLogger(ProcessDensity.class.getName()).log(Level.SEVERE, null, ex);
			}
			System.out.println("\n\n+++++"+Parameters.nameSetting[Parameters.iteratorSetting]+"++++\n\n");
		    }
		}
	    }
	} catch (IOException ex) {
	    Logger.getLogger(ProcessDensity.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private static String processDensityGenotype(String pathGenotype, String uniqueid, String pathExportImages) {

	System.out.println(pathGenotype);
	Parameters.isDensity = true;
	Statistics.densityStats = "";
	Parameters.endingGeneration = ParametersExport.densityEndingGeneration;
	LocalCell.percentageMutation = 0;
	LocalCell.decay = Parameters.paraDecay[Parameters.iDecay];
	LocalCell.puberty = Parameters.paraPuberty[Parameters.iPuberty];

	CAGird.initLGP();
	
	int x = 150, y = 150;
	CAGird.initCA(x, y);
	
	DetectCycle.initGird(x,y);
	CAGird.SetStatesCount(9);	
	Parameters.FolderDensity = pathExportImages;
	States.reinitPropa();
	return CAGird.runDensity(pathGenotype, uniqueid);
    }

}
