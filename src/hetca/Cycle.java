package hetca;

import java.util.Random;
import java.util.Stack;

class Cycle {

    public static byte[][] CAStatesTmpDeBug;
    public static String[][] CAStatesTmpDeBug2;
    public static int[] warperTorique = new int[4];
    public static int[] candidateRules = new int[5];
    public static int[] candidateRulesChances = new int[5];
    public static byte[] voisinage = new byte[9];
    public static Stack propagatedRules = new Stack();


    public static void OneGeneration() {

	Statistics.Population[Parameters.ISNORMAL] = 0;

	int i;

	for (i = 0; i < Statistics.Populations.length; i++) {
	    Statistics.Populations[i] = 0;
	}

	for (i = 0; i < candidateRules.length; i++) {
	    candidateRules[i] = -6;
	}

	mutateRules();

	doPropaAndDecay();

	Statistics.Populations[(LocalCell.getNumStates() - 1)] += Statistics.Populations[LocalCell.getNumStates()];

	setNextRule();

	GetNextState();

	setNextState();

	LocalRule.RazCounterGenotypeList();

	setFreqAndPositionsGenotypes();

	findMostComonRule();

	LocalRule.CleanTable(Statistics.nbrGenome, false);

    }

    static void OneGenerationDensity() {


	Statistics.Population[Parameters.ISNORMAL] = 0;

	int i;

	for (i = 0; i < Statistics.Populations.length; i++) {
	    Statistics.Populations[i] = 0;
	}

	for (i = 0; i < candidateRules.length; i++) {
	    candidateRules[i] = -6;
	}

	doPropaAndDecayDensity();

	Statistics.Populations[(LocalCell.getNumStates() - 1)] += Statistics.Populations[LocalCell.getNumStates()];

	GetNextState();

	setNextState();

    }

    private static void mutateRules() {

	for (int i = 0; i < Parameters.UnivSize.x; i++) {
	    for (int j = 0; j < Parameters.UnivSize.y; j++) {
		if (CAGird.CACells[i][j].isAlive() && (Math.random() < LocalCell.percentageMutation / 1250)) {
		    CAGird.CACells[i][j].randomiseNewman();
		}
	    }
	}
    }

    private static void doPropaAndDecay() {

	Random generator = new Random(System.nanoTime());

	for (int i = 0; i < Parameters.UnivSize.x; i++) {

	    setWarperToriqueX(i);

	    for (int j = 0; j < Parameters.UnivSize.y; j++) {

		voisinage[0] = CAGird.CACells[i][j].GetState();

		if (States.isnotdecay(voisinage[0])) {

		    setWarperToriqueY(j);

		    getAndSetVoisinage(i, j);

		    getNewRule(i, j, generator);

		    if (voisinage[0] != 0) {
			CAGird.CAStatesTmp[i][j] = CAGird.CACells[i][j].GetDecayAndAgeUp();
		    }
		}
	    }
	}
    }
    

    private static void doPropaAndDecayDensity() {

	for (int i = 0; i < Parameters.UnivSize.x; i++) {

	    setWarperToriqueX(i);

	    for (int j = 0; j < Parameters.UnivSize.y; j++) {

		voisinage[0] = CAGird.CACells[i][j].GetState();

		if (States.isnotdecay(voisinage[0])) {

		    setWarperToriqueY(j);

		    getAndSetVoisinage(i, j);

		    if (voisinage[0] == 0) {
			    getNewRuleDensity(i, j);
		    }

		    if (voisinage[0] != 0) {
			CAGird.CAStatesTmp[i][j] = CAGird.CACells[i][j].GetDecayAndAgeUp();
		    }
		}
	    }
	}
    }

    private static void GetNextState() {
	for (int i = 0; i < Parameters.UnivSize.x; i++) {
	    setWarperToriqueX(i);
	    for (int j = 0; j < Parameters.UnivSize.y; j++) {

		setWarperToriqueY(j);
		byte oldState = CAGird.CACells[i][j].GetStateWthAudoDecay();

		if (States.isDecay(oldState)) {
		    CAGird.CAStatesTmp[i][j] = oldState;
		    CAGird.CACells[i][j].ageUpSpe(CAGird.CAStatesTmp, i, j);
		} else {

		    getAndSetVoisinage(i, j);

		    if (States.isnotdecay(CAGird.CAStatesTmp[i][j])) {
			byte newState = 0; // par défault à ne pas virer
			if (oldState != 0) {
			    newState = CAGird.CACells[i][j].GetCellNewState(voisinage);
			} else if (CAGird.CACells[i][j].okChange == 1) {
			    newState = CAGird.CACells[i][j].GetCellNewState(voisinage);
			}
			CAGird.CAStatesTmp[i][j] = newState;
		    }
		    if ((CAGird.CAStatesTmp[i][j] == 0) && (CAGird.CACells[i][j].rulesnumber >= 0)) {
			CAGird.CACells[i][j].rulesnumber = -10;
		    }
		}
		CAGird.CACells[i][j].okChange = 0;
	    }
	}

    }

    private static void setNextState() {
	for (int i = 0; i < Parameters.UnivSize.x; i++) {
	    for (int j = 0; j < Parameters.UnivSize.y; j++) {
		CAGird.CACells[i][j].SetState(CAGird.CAStatesTmp[i][j]);
		Statistics.Populations[CAGird.CACells[i][j].GetStateWthAudoDecay()] += 1;
		if (CAGird.CACells[i][j].isAlive()) {
		    Statistics.Population[Parameters.ISNORMAL] += 1;
		}
		CAGird.CAStatesTmp[i][j] = 0;
	    }

	}
    }

    private static void setNextRule() {
	while (!propagatedRules.empty()) {
	    TempCell tmpCell = (TempCell) propagatedRules.pop();
	    CAGird.CACells[tmpCell.Xcoor][tmpCell.Ycoor].rulesnumber = tmpCell.rulesnumber;
	}
    }

    private static void setFreqAndPositionsGenotypes() {
	for (int i = 0; i < Parameters.UnivSize.x; i++) {
	    for (int j = 0; j < Parameters.UnivSize.y; j++) {
		if (CAGird.CACells[i][j].isAlive()) {
		    LocalRule.Fulllist[CAGird.CACells[i][j].rulesnumber].increaseNumNormal();
		    LocalRule.Fulllist[CAGird.CACells[i][j].rulesnumber].setXY(i, j);
		}
	    }
	}
    }

    private static void getAndSetVoisinage(int i, int j) {
	voisinage[2] = CAGird.CACells[i][warperTorique[1]].GetState();
	voisinage[3] = CAGird.CACells[i][warperTorique[3]].GetState();
	voisinage[4] = CAGird.CACells[warperTorique[0]][j].GetState();
	voisinage[1] = CAGird.CACells[warperTorique[2]][j].GetState();
	voisinage[0] = CAGird.CACells[i][j].GetState();

	voisinage[5] = CAGird.CACells[warperTorique[0]][warperTorique[1]].GetState();
	voisinage[6] = CAGird.CACells[warperTorique[2]][warperTorique[3]].GetState();
	voisinage[7] = CAGird.CACells[warperTorique[2]][warperTorique[1]].GetState();
	voisinage[8] = CAGird.CACells[warperTorique[0]][warperTorique[3]].GetState();
    }

    private static void setWarperToriqueX(int i) {

	if (i > 0) {
	    warperTorique[0] = (i - 1);
	} else {
	    warperTorique[0] = (Parameters.UnivSize.x - 1);
	}
	if (i < Parameters.UnivSize.x - 1) {
	    warperTorique[2] = (i + 1);
	} else {
	    warperTorique[2] = 0;
	}
    }

    private static void setWarperToriqueY(int j) {
	if (j > 0) {
	    warperTorique[1] = (j - 1);
	} else {
	    warperTorique[1] = (Parameters.UnivSize.y - 1);
	}
	if (j < Parameters.UnivSize.y - 1) {
	    warperTorique[3] = (j + 1);
	} else {
	    warperTorique[3] = 0;
	}
    }


    static void getNewRule(int x, int y, Random generator) {
	for(int i=0;i<candidateRulesChances.length;i++){
	    candidateRulesChances[i]=0;
	}
	if(States.isAlive(voisinage[0])){
	    candidateRules[0] = CAGird.CACells[x][y].rulesnumber;
	    candidateRulesChances[0] = States.propaChances(voisinage[0]);
	}
	
	int sumchances = candidateRulesChances[0];
	sumchances += setCellChancesToPropagate(1,warperTorique[2],y);
	sumchances += setCellChancesToPropagate(2,x,warperTorique[1]);
	sumchances += setCellChancesToPropagate(3,x,warperTorique[3]);
	sumchances += setCellChancesToPropagate(4,warperTorique[0],y);
	if(sumchances>0){
	    int selcel = generator.nextInt(sumchances);
	    sumchances = 0;
	    int selectedGenotype;
	    for(selectedGenotype=0;candidateRulesChances[selectedGenotype]+sumchances<=selcel;selectedGenotype++){
		sumchances+=candidateRulesChances[selectedGenotype];
	    }
	    if (States.isQuiescent(voisinage[0])) {
		CAGird.CACells[x][y].okChange = 1;
		CAGird.CACells[x][y].SetRuleNumber(candidateRules[selectedGenotype]);

	    } else {

		if (candidateRules[selectedGenotype] != candidateRules[0]) {
		    propagatedRules.push(new TempCell(x, y, candidateRules[selectedGenotype]));
		}
	    }
	}
    }
    
    static void getNewRuleDensity(int x, int y) {
	for(int i=0;i<candidateRulesChances.length;i++){
	    candidateRulesChances[i]=0;
	}
	
	int sumchances = candidateRulesChances[0];
	sumchances += setCellChancesToPropagate(1,warperTorique[2],y);
	sumchances += setCellChancesToPropagate(2,x,warperTorique[1]);
	sumchances += setCellChancesToPropagate(3,x,warperTorique[3]);
	sumchances += setCellChancesToPropagate(4,warperTorique[0],y);
	if(sumchances>0){
	    CAGird.CACells[x][y].okChange = 1;
	    CAGird.CACells[x][y].SetRuleNumber(0);
	}
    }

    private static void findMostComonRule() {
	int itor, iter;
	Statistics.nbrGenome = 0;
	int[] isMax = new int[5];
	int[] valMax = new int[5];
	for (int i = 0; i < 5; i++) {
	    isMax[i] = -1;
	    valMax[i] = 0;
	}
	for (int i = 0; i < LocalRule.Fulllist.length; i++) {
	    if (LocalRule.Fulllist[i] != null) {
		Statistics.nbrGenome++;
		iter = 0;
		while (iter < valMax.length - 1 && LocalRule.Fulllist[i].frequenceNormal > valMax[iter + 1]) {
		    iter++;
		}
		for (itor = 0; itor < iter; itor++) {
		    isMax[itor] = isMax[itor + 1];
		    valMax[itor] = valMax[itor + 1];
		}
		if (LocalRule.Fulllist[i].frequenceNormal > valMax[iter]) {
		    isMax[itor] = i;
		    valMax[itor] = LocalRule.Fulllist[i].frequenceNormal;
		}
	    }
	}

	for (int i = 0; i < valMax.length; i++) {
	    if (isMax[i] > -1) {
		LocalRule.Fulllist[isMax[i]].setMax();
	    }
	}
	Statistics.maxPopGenome = valMax[valMax.length - 1];
    }

    private static int setCellChancesToPropagate(int i, int x, int y) {
	candidateRules[i] = CAGird.CACells[x][y].rulesnumber;
	return    candidateRulesChances[i] = CAGird.CACells[x][y].getPropaChance(voisinage,i);
    }
}
