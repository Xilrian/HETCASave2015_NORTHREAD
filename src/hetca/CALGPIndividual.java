
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hetca;

import ca.kowaliw.gp.GPUtils;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import ca.kowaliw.gp.GPIndividual;
import ca.kowaliw.gp.GPParams;
import ca.kowaliw.utils.TU;
import java.util.logging.Level;
import java.util.logging.Logger;


/** 
 * @author Taras Kowaliw, http://kowaliw.ca, Jan 2010 */
public class CALGPIndividual implements GPIndividual {

    int numInputs;
    public int getNumInputs() { return numInputs; }
    int numStates;
    public int getNumStates() { return numStates; }
    
    int numAdditionalRegisters;
    public int getNumAdditionalRegisters() { return numAdditionalRegisters; }
    
    int length;
    int numRulesUsed;
    public String title = "untitled";
    
    private double maxGeneticValue;
    
    // stores the LGP rules
    public CALGPRule[] theRules;
    
    
    public double[] registerConsts;
    
    private boolean[] isRuleUsed;
    private boolean[] isRegisterUsed;
    
    // ################################################################
    // ########### CONSTRUCTORS AND INITIALIZERS ######################
    
    /** Creates a random LGP inidividual. numAdditionalRegisters *includes* numStates. */
    public CALGPIndividual(int numInputs, int numStates, int numAdditionalRegisters) {
        this.numInputs = numInputs;
        this.numStates = numStates;
        this.numAdditionalRegisters = numAdditionalRegisters;
        
        maxGeneticValue = Math.max(numInputs, numAdditionalRegisters);
        
        length = TU.gen.nextInt(GPParams.maxInitLength) + 1;
        
        registerConsts = new double[numAdditionalRegisters];
        for (int wR = 0; wR < registerConsts.length; wR++) {
            registerConsts[wR] = Math.round(TU.gen.nextDouble()*maxGeneticValue);
            if (TU.gen.nextBoolean()) registerConsts[wR] =  -1.0*registerConsts[wR];
            //if (TU.gen.nextDouble() < 0.5) registerConsts[wR] = registerConsts[wR]*GPParams.maxLength;
        }
        
        theRules = new CALGPRule[length];
        for (int wR = 0; wR < length; wR++) {
            theRules[wR] = new CALGPRule(numInputs, numAdditionalRegisters);
        }
        
        initializeRedundancyArrays();
    }
    
    /** Creates an LGPIdividual from the provided stuff. 
     * Note: uses someRules directly, i.e. make a clone before passing it. 
     * numAdditionalRegisters *includes* numStates. */
    public CALGPIndividual(int numInputs, int numStates, int numAdditionalRegisters, int length, double[] registerConsts, CALGPRule[] theRules) {
        this.numInputs = numInputs;
        this.numStates = numStates;
        this.numAdditionalRegisters = numAdditionalRegisters;

    	this.length = length;
    	this.registerConsts = registerConsts;
    	this.theRules = theRules;
        
        initializeRedundancyArrays();
    }
    
    /** Creates an LGPIndividual from a String of the type returned by toString(). */
    public CALGPIndividual(String genotype) {
        initializeFromString(genotype);
        initializeRedundancyArrays();
    }
    
    /** Creates an LGPIndividual from a File (.lgpdna) containing a single
     * String of the type returned by toString(). */
    public CALGPIndividual(File dnaFile) {
        String genotype = "";
        try {
            BufferedReader input =  new BufferedReader(new FileReader(dnaFile));
            String line;
            while (( line = input.readLine()) != null) {
                if (!line.startsWith("#")) genotype = line;
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            System.exit(0);
        }
        
        initializeFromString(genotype);
        initializeRedundancyArrays();
    }    
    
    /** Does the actual work in creating an individual from a String. */
    private void initializeFromString(String genotype) {
    	String[] theBits = genotype.split("&&&");
    	this.length = GPUtils.intFromString(theBits[0]);
    	this.title = theBits[1];
        this.numInputs = GPUtils.intFromString(theBits[2]);
        this.numStates = GPUtils.intFromString(theBits[3]);
        this.numAdditionalRegisters = GPUtils.intFromString(theBits[4]);

    	
    	registerConsts = new double[numAdditionalRegisters];
    	for (int wR = 0; wR < registerConsts.length; wR++) 
    		registerConsts[wR] = GPUtils.doubleFromString(theBits[5+wR]);
    	
    	theRules = new CALGPRule[length];
    	for (int wR = 0; wR < theRules.length; wR++) theRules[wR] = new CALGPRule(theBits[5 + numAdditionalRegisters + wR]);
    }
    
    /** Creates a clone of this object */
    public CALGPIndividual clone() {
    	double[] newConsts = new double[registerConsts.length];
    	for (int wR = 0; wR < registerConsts.length; wR++) newConsts[wR] = registerConsts[wR];
    	
    	CALGPRule[] newRules = new CALGPRule[theRules.length];
    	for (int wR = 0; wR < theRules.length; wR++) try {
	    newRules[wR] = theRules[wR].clone();
		} catch (CloneNotSupportedException ex) {
		    Logger.getLogger(CALGPIndividual.class.getName()).log(Level.SEVERE, null, ex);
		}
   	
    	return new CALGPIndividual(numInputs, numStates, numAdditionalRegisters, length, newConsts, newRules);
    }
    
    /** Returns a String representation, suitable for loading via the constructor. 
	 * Use this for saving to file. */
    public String toString() {
    	String outStr = "";
    	outStr += length+"&&&";
    	outStr += title+"&&&";
    	outStr += numInputs+"&&&";
    	outStr += numStates+"&&&";
    	outStr += numAdditionalRegisters+"&&&";
    	for (int wR = 0; wR < registerConsts.length; wR++) 
    		outStr += registerConsts[wR] + "&&&";
    	for (int wR = 0; wR < theRules.length; wR++) 
    		outStr += theRules[wR].toString() + "&&&";
    	return outStr;
    }
    
    
    
    // #################################################################
    // Genetic Operators

    /** Changes the number of inputs to the new value. */
    public void changeNumInputs(int newNumInputs) {
        numInputs = newNumInputs;
     	for (int wR = 0; wR < theRules.length; wR++)
    		theRules[wR].changeNumInputs(newNumInputs);
        initializeRedundancyArrays();
    }
    
    
    
    
    
    /** Returns a mutated child. Mutation is point-mutation, applied at every node
     * w/ prob pM. */
    public GPIndividual mutate(double pM) {
    
        
    	// mutate the constant values...
    	double[] newRegisterConsts = new double[registerConsts.length];
    	for (int wR = 0; wR < registerConsts.length; wR++) {
    		newRegisterConsts[wR] = registerConsts[wR];
    		if (TU.gen.nextDouble() < pM) {
                    double change = Math.round(TU.gen.nextGaussian()*3);

                    double newVal = newRegisterConsts[wR] + change;
                    if (newVal > this.maxGeneticValue) newVal = this.maxGeneticValue;
                    if (newVal < -1.0*this.maxGeneticValue) newVal = -1.0*this.maxGeneticValue;
                    newRegisterConsts[wR]  = newVal;                    
    		}
    	}        
        
        // now mutate the rules...
    	int newLength = length;
        CALGPRule[] newRules = null;


        double theDie = TU.gen.nextDouble();
        
        // shrink it...
    	if (theDie < 0.35 && newLength > 1) {
            newLength --;
            newRules = new CALGPRule[newLength];
            
            int skip = TU.gen.nextInt(length);
            int countOld = 0;
            for (int countNew = 0; countNew < newLength; countNew++) {
                if (countOld == skip) {
                    countOld++;
                }
		try {
		    newRules[countNew] = theRules[countNew].clone();
		} catch (CloneNotSupportedException ex) {
		    Logger.getLogger(CALGPIndividual.class.getName()).log(Level.SEVERE, null, ex);
		}
                countOld++;
            }
                
                
        // grow it...
    	} else if (theDie < 0.5 && newLength < GPParams.maxLength - 1) {
            newLength++;
            newRules = new CALGPRule[newLength];
            int skipMax = length -1;
            if (skipMax < 1) skipMax = 1;
            int skip = TU.gen.nextInt(skipMax);//(int) (Math.random()*(length-1));
            int countOld = 0;
            for (int countNew = 0; countNew < newLength; countNew++) {
                if (countNew == skip) {
                    newRules[countNew] = new CALGPRule(numInputs, numAdditionalRegisters);
                    countNew++;
                }
		try {
		    newRules[countNew] = theRules[countOld].clone();
		} catch (CloneNotSupportedException ex) {
		    Logger.getLogger(CALGPIndividual.class.getName()).log(Level.SEVERE, null, ex);
		}
                countOld++;
            }
                
        // same size...
    	} else {
            newRules = new CALGPRule[newLength];
            for (int wR = 0; wR < newLength; wR++) {
                newRules[wR] = theRules[wR].mutate(pM);
            } 
        }

    	return new CALGPIndividual(numInputs, numStates, numAdditionalRegisters, newLength, newRegisterConsts, newRules);
    }
    
    /** Returns a child, the product of single-point crossover,
     * of length equal to that of the second parent. */
    public GPIndividual cross(GPIndividual thatG) {
        CALGPIndividual that = (CALGPIndividual) thatG;
        
        int minLength = (int) Math.min(this.length, that.length);
        int crossPoint = TU.gen.nextInt(minLength);//(int) (Math.random()*minLength);
        int newLength = that.length;
        
        // copy the first individual's constants
    	double[] newRegisterConsts = new double[registerConsts.length];
    	for (int wR = 0; wR < registerConsts.length; wR++) {
    		newRegisterConsts[wR] = this.registerConsts[wR];
    	} 
        
        // copy the first individual's rules up to the crosspoint,
        // then start on the second individual's rules.
        CALGPRule[] newRules = new CALGPRule[newLength];
        for (int wR = 0; wR < newLength; wR++) {
            if (wR < crossPoint)
                try {
		    newRules[wR] = this.theRules[wR].clone();
	    } catch (CloneNotSupportedException ex) {
		Logger.getLogger(CALGPIndividual.class.getName()).log(Level.SEVERE, null, ex);
	    }
            else
                try {
		    newRules[wR] = that.theRules[wR].clone();
	    } catch (CloneNotSupportedException ex) {
		Logger.getLogger(CALGPIndividual.class.getName()).log(Level.SEVERE, null, ex);
	    }
        }
        
        // send that puppy home!
        return new CALGPIndividual(numInputs, numStates, numAdditionalRegisters, newLength, newRegisterConsts, newRules);
    }
    
    
    
    
    
    
    
    /** Computes which registers / rules are redundant. */
    public void initializeRedundancyArrays() {
        
        isRuleUsed = new boolean[length];
        isRegisterUsed = new boolean[numInputs + numAdditionalRegisters];
        
        // set the state registers as true
        for (int wR = 0; wR < numStates; wR++) {
            
            isRegisterUsed[numInputs + wR] = true;
        }   
        
        
        for (int wR = length-1; wR >= 0; wR--) {
            if (isRegisterUsed[theRules[wR].targetRegister]) {
                
                isRegisterUsed[theRules[wR].targetRegister] = false;
                ca.kowaliw.gp.GPOperator thisOp = GPUtils.getOp(theRules[wR].opCode);  //.theOps[theRules[wR].opCode];
                
                if (thisOp.getNumArgs() >= 1)
                    isRegisterUsed[theRules[wR].arg1Register] = true;
                if (thisOp.getNumArgs() >= 2)
                    isRegisterUsed[theRules[wR].arg2Register] = true;
                isRuleUsed[wR] = true;
            }
        }
        
        // now count the number of used rules
        numRulesUsed = 0;
        for (int wR = 0; wR < theRules.length; wR++) if (isRuleUsed[wR]) numRulesUsed++;
    }
    
    /** Returns the length of the individual */
    public int getTotalSize() {
        return this.length;
    }

    /** Returns the effective length (note: NO structural
     * or semantic intron removal! Just eliminating unconnected nodes.  */
    public int getEffectiveSize() {
        initializeRedundancyArrays();
        return numRulesUsed;
    }
    
    /** Gets the output of the program, given the input. */
    public double apply(byte[] inputs) {
        if (inputs.length != numInputs) {
            System.out.println("ERROR!! Incorrent num inputs in LGPIndividual.apply");
            return -1.0;
        }
        
        // create a place to store the register values
        double[] registers = new double[numInputs + numAdditionalRegisters];
        double output = 0.0;

        //System.out.println("Apply of " + inputs[0] +", "+inputs[1]);
        
        // load in the inputs and the constants
        for (int wR = 0; wR < numInputs; wR++) {
            registers[wR] = inputs[wR];
            //System.out.println("Set reg. "+wR+" to "+registers[wR]);
        }
        for (int wR = 0; wR < numAdditionalRegisters; wR++) {
            registers[wR + numInputs] = registerConsts[wR];
            //System.out.println("Set reg. "+wR+" to "+registers[wR]);
        }
        
        // now compute the rule values
        for (int wR = 0; wR < length; wR++) {
            if (isRuleUsed[wR])
                registers[theRules[wR].targetRegister] = 
                    GPUtils.applyOp(
                        theRules[wR].opCode,
                        registers[theRules[wR].arg1Register],
                        registers[theRules[wR].arg2Register]
                    );
            //System.out.println("Set reg. "+theRules[wR].targetRegister+" to "+registers[theRules[wR].targetRegister]);
        }

        // find the state register with the highest value
        int maxRegisterNumber = numInputs;
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int wR = numInputs; wR < numStates + numInputs; wR++) {
            if (registers[wR] > maxValue) {
                maxRegisterNumber = wR;
                maxValue = registers[wR];
            }
        }
        // replace the register nmber with a number between 0 and numStates - 1
        maxRegisterNumber -= numInputs;
        
        
        /*output = registers[theRules[length-1].targetRegister];

        if (GPParams.useBoundaries) {
            if (Double.isNaN(output)) return GPParams.maxBoundary;
            else if (output < GPParams.minBoundary) return GPParams.minBoundary;
            else if (output > GPParams.maxBoundary) return GPParams.maxBoundary;
            return output;
        }*/
        return maxRegisterNumber;
    }
    
    /** Gets the output of the program, given the input. */
    public double apply(double[] inputs) {
        if (inputs.length != numInputs) {
            System.out.println("ERROR!! Incorrent num inputs in LGPIndividual.apply");
            return -1.0;
        }
        
        // create a place to store the register values
        double[] registers = new double[numInputs + numAdditionalRegisters];
        double output = 0.0;

        //System.out.println("Apply of " + inputs[0] +", "+inputs[1]);
        
        // load in the inputs and the constants
        for (int wR = 0; wR < numInputs; wR++) {
            registers[wR] = inputs[wR];
            //System.out.println("Set reg. "+wR+" to "+registers[wR]);
        }
        for (int wR = 0; wR < numAdditionalRegisters; wR++) {
            registers[wR + numInputs] = registerConsts[wR];
            //System.out.println("Set reg. "+wR+" to "+registers[wR]);
        }
        
        // now compute the rule values
        for (int wR = 0; wR < length; wR++) {
            if (isRuleUsed[wR])
                registers[theRules[wR].targetRegister] = 
                    GPUtils.applyOp(
                        theRules[wR].opCode,
                        registers[theRules[wR].arg1Register],
                        registers[theRules[wR].arg2Register]
                    );
            //System.out.println("Set reg. "+theRules[wR].targetRegister+" to "+registers[theRules[wR].targetRegister]);
        }

        // find the state register with the highest value
        int maxRegisterNumber = numInputs;
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int wR = numInputs; wR < numStates + numInputs; wR++) {
            if (registers[wR] > maxValue) {
                maxRegisterNumber = wR;
                maxValue = registers[wR];
            }
        }
        // replace the register nmber with a number between 0 and numStates - 1
        maxRegisterNumber -= numInputs;
        
        
        /*output = registers[theRules[length-1].targetRegister];

        if (GPParams.useBoundaries) {
            if (Double.isNaN(output)) return GPParams.maxBoundary;
            else if (output < GPParams.minBoundary) return GPParams.minBoundary;
            else if (output > GPParams.maxBoundary) return GPParams.maxBoundary;
            return output;
        }*/
        return maxRegisterNumber;
    }
    
    /** Generates a LaTeX String from the program. Removes introns (inefficiently). */
    public String toLaTeXString() {
        String[] registers = new String[numInputs + numAdditionalRegisters];
        String outLine = "";
        
        // load in the inputs and the constants
        for (int wR = 0; wR < numInputs; wR++) {
                 registers[wR] = "i_{"+wR+"}";
        }
        outLine = "o(x_{0}, ..., x_{"+(numInputs-1)+"}) = ";
     
        for (int wR = 0; wR < numAdditionalRegisters; wR++) {
            registers[wR+numInputs] = GPUtils.df.format(registerConsts[wR]);
        }        
        
        // now compute the rule values
        for (int wR = 0; wR < length; wR++) {
            registers[theRules[wR].targetRegister] = 
                    GPUtils.opLaTeXString(
                        theRules[wR].opCode,
                        registers[theRules[wR].arg1Register],
                        registers[theRules[wR].arg2Register]
                    );
        }      
        
        outLine += "argmax \\left\\{  ";
        for (int wR = numInputs; wR < numStates + numInputs; wR++) {
            if (wR != numInputs) outLine += ", ";
            outLine += registers[wR];
        }
        outLine += " \\right\\}";
        return outLine;
        
        // use arg max instead
        //return outLine + registers[theRules[length-1].targetRegister];
    }
    
    
    /** Creates a buffered image version of the output of this program. NOTE: only works if numInputs = 2. */
    public BufferedImage createImageVersion(int size) {
        BufferedImage theImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB); 
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double dx = 2.0*((double) x / (double) size) - 1.0;
                double dy = 1.0 - 2.0*((double) y / (double) size);
                //double dx = ((double) x / (double) size);
                //double dy = 1.0 - ((double) y / (double) size);
                double[] in = {dx, dy};
                double output = apply(in);
                if (output > 1.0) output = 1.0;
                if (output < 0.0) output = 0.0;
                int greyVal = (int) (255.0 * output);
                
                Color temp = new Color(greyVal, greyVal, greyVal);
                theImage.setRGB(x, y, temp.getRGB());  
            }
        }
        
        return theImage;
    }
    
    /** Saves this individual as xfourx three files, in the (possibly null) directory parentDir: [fileNameStart].tex, .lgpdna, .png (but not .lgpcode, iuncomment if you want it) */
    public void saveTheLot(File parentDir, String fileNameStart, int size) {
        try {
        	
        	PrintWriter theWriter = new PrintWriter(new File(parentDir, fileNameStart+".tex"));
    		theWriter.println(toLaTeXString());
    		theWriter.close();
    		
    		theWriter = new PrintWriter(new File(parentDir, fileNameStart+".lgpdna"));
    		theWriter.println(toString());
    		theWriter.close();

    		theWriter = new PrintWriter(new File(parentDir, fileNameStart+".lgpcode"));
    		theWriter.println(toCodeString());
    		theWriter.close();    		
    		
            javax.imageio.ImageIO.write(createImageVersion(size), "PNG", new File(parentDir,  fileNameStart+".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }    	
    	
    }  
    
    public void saveImageVersion(File parentDir, String fileName, int size) {
        try {
            javax.imageio.ImageIO.write(createImageVersion(size), "PNG", new File("output", fileName+".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /** Outputs a pseudo-code version of the program. Introns included. */
    public String toCodeString() {
        String outStr = "LGP {" + TU.endl;
        outStr += "   length: "+length + TU.endl;
        
        for (int wR = 0; wR < numInputs; wR++) {
                if (isRegisterUsed[wR]) outStr += "   ";
                else outStr += "   //";
                outStr += "R" + wR + " = " + "i_{" + wR + "};" + TU.endl; 
        }
        
        for (int wR = 0; wR < numAdditionalRegisters; wR++) {

            
            if (isRegisterUsed[wR + numInputs]) outStr += "   ";
            else outStr += "   //";
            outStr += "R" + (wR+numInputs) + " = " + GPUtils.df.format(registerConsts[wR])+";"; 
            
            // mark it as a state register
            if (wR < numStates) outStr += " // state register "+wR;  
            
            outStr += TU.endl;
        }
        for (int wR = 0; wR < length; wR++) {
            if (isRuleUsed[wR]) outStr += "   ";
            else outStr += "   //";
            outStr += "" + theRules[wR].toCodeString() + TU.endl; 
        }
        //outStr += "   return R"+theRules[length-1].targetRegister+";" + TU.endl;
        
        outStr += "   return argmax(";
        for (int wR = numInputs; wR < numStates + numInputs; wR++) {
            if (wR != numInputs) outStr += ", ";
            outStr += "R"+wR;
        }
        outStr += ");" + TU.endl;
        
        
        return outStr+"}\n";
    }
}
