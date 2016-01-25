/*
 * CA-LGP Test  class
 */
package hetca;


import ca.kowaliw.gp.GPParams;
import ca.kowaliw.gp.GPUtils;
import ca.kowaliw.utils.TU;

/**
 *
 * @author T. Kowaliw, http://kowaliw.ca
 */
public class CALGP_Test {

    /**
     * @param args the command line arguments
     */ 
    public static void main(String[] args) {
        
        
        int numInputs = 8; // i.e. the size of the nbhd
        int numStates = 4; // i.e. the size of the alphabet
        int numAdditionalRegisters = numStates + 2;
        
        GPParams.maxInitLength = 50;
        GPParams.maxLength = 50;
        
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
        
        CALGPIndividual anInd = new CALGPIndividual(
                numInputs,
                numStates, 
                numAdditionalRegisters);
        
        System.out.println(anInd.toCodeString());
        System.out.println(anInd.toLaTeXString());
        
        
        
        
        String dnaString = anInd.toString();
        CALGPIndividual copyInd = new CALGPIndividual(dnaString);
        System.out.println("Copied Individual"); 
        System.out.println(copyInd.toCodeString());
        
        

        CALGPIndividual mutatedInd = (CALGPIndividual) copyInd.mutate(0.1);
        System.out.println("Mutated Individual"); 
        System.out.println(mutatedInd.toCodeString());

        
        showSomeVals(anInd);
        
        System.exit(0);
    }
    
    public static void showSomeVals(CALGPIndividual anInd) {
        System.out.println(""); 
        System.out.println("20 random values: "); 
        for (int i=0; i< 20; i++) {
            System.out.print("   " + TU.numify(i) + ": ");
            double[] vals = new double[anInd.getNumInputs()];
            for (int wI = 0; wI < vals.length; wI++) {
                vals[wI] = TU.gen.nextInt(anInd.getNumStates());
                System.out.print(vals[wI]+" ");
            }
            double output = anInd.apply(vals);
            System.out.println(" -> "+output); 
        }
    } 
}
