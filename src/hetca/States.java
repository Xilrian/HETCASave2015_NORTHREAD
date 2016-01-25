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
class States {
    final static int DECAY =  LocalCell.getNumStates()-1;
    final static int AUTODECAY =  LocalCell.getNumStates();
    final static int QUIESCENT =  0;
    private static int[] propaChances={0,1,1,1,1,1,0,0};

    static boolean isnotdecay(byte state) {
	return state < LocalCell.getNumStates() - 1;
    }

    static boolean isAlive(byte state) {
	return state < LocalCell.getNumStates() - 1 && state != 0;
    }

    static boolean isQuiescent(byte state) {
	return state == 0;
    }

    static boolean isDecay(byte state) {
	return state >= LocalCell.getNumStates() - 1;
    }

    static int propaChances(byte state) {
	return propaChances[state];
    }

    static void updatePropagationChances() {
	int transitionStage = (CAGird.Generation+10)%Parameters.getPropaCycle()+1;
	if(transitionStage<=Parameters.getTransition() && CAGird.Generation>Parameters.getMinCycleUpPropa()){
	    for(int i=0;i<propaChances.length;i++){
		propaChances[i]= (Parameters.getTransition()-transitionStage)*Parameters.getPreviouspropaChancesEvo(i)+(transitionStage)*Parameters.getNewpropaChancesEvo(i);
		System.out.println(i+"-"+propaChances[i]);
	    }
	    
	    displayPropa();
	}
    }

    private static void displayPropa() {
	Parameters.checkTransition();
	System.out.println("New Propa :");
	for(int i=0;i<propaChances.length;i++){
	    System.out.print(propaChances[i]+"-");
	}
	    System.out.println("+++++");
    }
    
    static void reinitPropa() {
	for(int i =0;i<propaChances.length;i++){
	    propaChances[i] = Parameters.propaInit(i);
	}
	    System.out.println("INIT :");
	    displayPropa();
    }
    
}
