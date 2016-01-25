/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hetca;

import ca.kowaliw.gp.GPUtils;
import ca.kowaliw.utils.TU;

/** 
 * @author Taras Kowaliw, http://kowaliw.ca, Jan 2010 */
public class CALGPRule {

    public int targetRegister = -1;
    public int opCode = -1;
    public int arg1Register = -1;
    public int arg2Register = -1;

    int numInputs;
    int numAdditionalRegisters;
    
    /** Creates a random LGP rule.
     * @param numInputs
     * @param numAdditionalRegisters */
    public CALGPRule(int numInputs, int numAdditionalRegisters) {
        this.numInputs = numInputs;
        this.numAdditionalRegisters = numAdditionalRegisters;

        targetRegister = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
        arg1Register = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
        arg2Register = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
        opCode = GPUtils.getRandomOp();
    }
    
    /** Creates an LGP rule w/ the supplied params.
     * @param numInputs
     * @param numAdditionalRegisters
     * @param targetRegister
     * @param opCode
     * @param arg1Register
     * @param arg2Register */
    public CALGPRule(int numInputs, int numAdditionalRegisters, int targetRegister, int opCode, int arg1Register, int arg2Register) {
        this.numInputs = numInputs;
        this.numAdditionalRegisters = numAdditionalRegisters;

        this.targetRegister = targetRegister;
        this.opCode = opCode;
        this.arg1Register = arg1Register;
        this.arg2Register = arg2Register;        
    }
    
    /** Creates a new LGP Rule from the provided string, as produced by toString().
     * @param fromStr */
    public CALGPRule(String fromStr) {
    	String[] theBits = fromStr.split("%%%");
    	this.targetRegister = GPUtils.intFromString(theBits[0]);
    	this.opCode = GPUtils.intFromString(theBits[1]);
    	this.arg1Register = GPUtils.intFromString(theBits[2]);
    	this.arg2Register = GPUtils.intFromString(theBits[3]); 
        this.numInputs = GPUtils.intFromString(theBits[4]); 
        this.numAdditionalRegisters = GPUtils.intFromString(theBits[5]);
    }
    
    /** Creates a string suitable for inclusion in the saved genome of an LGPIndividual.
     * @return  */
    @Override
    public String toString() {
    	return targetRegister + "%%%" + opCode + "%%%" + arg1Register + "%%%" + arg2Register + "%%%" + numInputs + "%%%" + numAdditionalRegisters;
    }
    
    /** Creates a copy of the LGP rule.
     * @return 
     * @throws java.lang.CloneNotSupportedException */
    @Override
    public CALGPRule clone() throws CloneNotSupportedException {
        return new CALGPRule(numInputs, numAdditionalRegisters, targetRegister, opCode, arg1Register, arg2Register);
    }
    
    /** Creates a mutation of the LGP rule.
     * @param pM
     * @return  */
    public CALGPRule mutate(double pM) {
    	int tR = targetRegister;
    	if (TU.gen.nextDouble() < pM) tR = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
    	int oC = opCode;
    	if (TU.gen.nextDouble() < pM) oC = GPUtils.getRandomOp();
    	int a1 = arg1Register;
    	if (TU.gen.nextDouble() < pM) a1 = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
    	int a2 = arg2Register;
    	if (TU.gen.nextDouble() < pM) a2 = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
        return new CALGPRule(numInputs, numAdditionalRegisters, tR, oC, a1, a2);
    }    

    public void changeNumInputs(int newNum) {
        numInputs = newNum;
        if (targetRegister >= numInputs + numAdditionalRegisters) targetRegister = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
        if (arg1Register >= numInputs + numAdditionalRegisters) arg1Register = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
        if (arg2Register >= numInputs + numAdditionalRegisters) arg2Register = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
    }

    public void changeNumAdditionalRegisters(int newNum) {
        numAdditionalRegisters = newNum;
        if (targetRegister >= numInputs + numAdditionalRegisters) targetRegister = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
        if (arg1Register >= numInputs + numAdditionalRegisters) arg1Register = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
        if (arg2Register >= numInputs + numAdditionalRegisters) arg2Register = GPUtils.randomRegister(numInputs, numAdditionalRegisters);
    }

    public String toCodeString() {
        return GPUtils.opJavaString(opCode, targetRegister, arg1Register, arg2Register);
    }
}
