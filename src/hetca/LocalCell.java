package hetca;

public class LocalCell
	implements Cloneable {

    private static final byte numstate = 7;
    static float propagation = 100.0F;
    public static float percentageMutation = 100.0F;
    public static float spawning = 100.0F;
    static int [] lExpectency = {0,7,7,7,7,7};
    static int puberty = 1;
    static int decay = 0;
    private byte state;
    public int rulesnumber;
    private int life = 0;
    public short okChange;
    private int decaytime = 100;
    private final short Xpos;
    private final short Ypos;

    public LocalCell(short Xpos, short Ypos, boolean rulemode) {
	if (rulemode) {
	 //   this.rulesnumber = LocalRule.addNewRule();
	    this.rulesnumber = -10;
	} else {
	    this.rulesnumber = -10;
	}
	this.Xpos = Xpos;
	this.Ypos = Ypos;
    }

    static int getNumStates() {
	return numstate;
    }
    
    public short getNextX() {
	short ret = (short) (this.Xpos + 1);
	if (ret > Parameters.UnivSize.x - 1) {
	    ret = 0;
	}
	return ret;
    }

    public short getLastX() {
	short ret = (short) (this.Xpos - 1);
	if (ret < 0) {
	    ret = (short) (Parameters.UnivSize.x - 1);
	}
	return ret;
    }

    public short getNextY() {
	short ret = (short) (this.Ypos + 1);
	if (ret > Parameters.UnivSize.y - 1) {
	    ret = 0;
	}
	return ret;
    }

    public short getLastY() {
	short ret = (short) (this.Ypos - 1);
	if (ret < 0) {
	    ret = (short) (Parameters.UnivSize.y - 1);
	}
	return ret;
    }

    public boolean SetState(byte newState) {
	if (newState >= numstate - 1 && this.state!=newState) {
	    setForDecay();
	    if (decay == 0) {
		this.state = 0;
	    }
	} else if (newState == 0) {
	    this.life = 0;
	}
	this.state = newState;

	return true;
    }

    public byte GetDecayAndAgeUp() {
	this.life += 1;


	byte ret = this.state;
	if (this.life > lExpectency[this.state] ) {
	    ret = (byte) (numstate - 1);
	}
	return ret;
    } 

    private void setForDecay() {
	this.life = 0;
	this.decaytime = ((int) (decay - 3 * decay / 4 + Math.random() * decay));
    }

    public byte GetStateNoD() {
	byte ret = this.state;
	if (ret >= numstate) {
	    ret = (byte) (numstate - 1);
	}
	if (ret == numstate - 1) {
	    ret = 0;
	}
	return ret;
    }

    public byte GetStateWthAudoDecay() {
	byte ret = this.state;
	if (ret > numstate) {
	    ret = (byte) (numstate - 1);
	}
	return ret;
    }

    public byte GetState() {
	byte ret = this.state;
	if (ret >= numstate) {
	    ret = (byte) (numstate - 1);
	}
	return ret;
    }

    public byte GetStateDebug() {
	byte ret = this.state;

	return ret;
    }

    public int GetLife() {
	return this.life;
    }

    void randomSetAll(byte newState, short numliste) {
	if ((newState > 0) && (newState < numstate - 1)) {
	    if(Parameters.isCompare){
		    this.rulesnumber = (int) (Math.random()*2);
	    }
	    else if(Parameters.isDensity){
		    this.rulesnumber = 0;
	    }
	    else{
		    this.rulesnumber = LocalRule.addNewRule();
	    }
	}
	else {
	    this.rulesnumber = -10;
	}
	this.life = 0;
	this.okChange = 0;
	this.decaytime = ((int) (decay - 3 * decay / 4 + Math.random() * decay));
	SetState(newState);
    }

    void SetCon(LocalCell copysource) {
	this.rulesnumber = copysource.rulesnumber;
    }
    

    int getPropaChance(byte[] voisinage, int i) {
	
	int ret = 0;
	if(States.isAlive(voisinage[i])){
	    byte nextVal = GetCellNewState(voisinage);
	    if (States.isAlive(nextVal) && this.life > puberty) {
		ret = States.propaChances(voisinage[i]);
	    }
	}
	return ret;
    }

    byte GetCellNewState(byte[] iCnt) {
	byte ret = LocalRule.Fulllist[this.rulesnumber].GetNewStateFromRule(iCnt);

	if (ret == numstate - 1) {
	    ret = numstate;
	}

	return ret;
    }

    public void randomiseNewman() {
	this.rulesnumber = LocalRule.Fulllist[this.rulesnumber].randomise(this.rulesnumber);
    }

    void ageUpSpe(byte[][] tmpState, int i, int j) {
	this.life += 1;
	if (this.life > this.decaytime) {
	    this.life = 0;
	    tmpState[i][j] = 0;
	}
    }

    int GetStateVarPrime() {
	//  System.out.println("GetStateVarPrime");
	int finali = 0;
	double ret = 0.0D;
	double last = 0.0D;
	try {
	    byte[] liststate = {CAGird.CACells[getLastX()][getLastY()].GetStateNoD(), CAGird.CACells[getLastX()][this.Ypos].GetStateNoD(), CAGird.CACells[getLastX()][getNextY()].GetStateNoD(), CAGird.CACells[this.Xpos][getLastY()].GetStateNoD(), CAGird.CACells[this.Xpos][this.Ypos].GetStateNoD(), CAGird.CACells[this.Xpos][getNextY()].GetStateNoD(), CAGird.CACells[getNextX()][getLastY()].GetStateNoD(), CAGird.CACells[getNextX()][this.Ypos].GetStateNoD(), CAGird.CACells[getNextX()][getNextY()].GetStateNoD()};

	    int[] allCnt = new int[numstate];

	    for (int i = 0; i < allCnt.length; i++) {
		allCnt[i] = 0;
	    }

	    for (int i = 0; i < liststate.length; i++) {
		allCnt[liststate[i]] += 1;
	    }
	    allCnt[0] = ((byte) (allCnt[(numstate - 1)] + allCnt[0]));
	    allCnt[(numstate - 1)] = 0;

	    for (int i = 0; i < liststate.length; i++) {
		last += Statistics.PopVal[liststate[i]];
	    }

	    double moyenne = last / 9.0D;

	    for (int i = 0; i < liststate.length; i++) {
		ret += Math.pow(Statistics.PopVal[liststate[i]] - moyenne, 2.0D);
	    }
	} catch (Exception ex) {
	    System.out.println("exception " + ex);
	}

	int retour = (int) (40.0D * Math.sqrt(ret / 9.0D));
	if (retour > 20) {
	    retour = 20;
	} else if (retour < 1) {
	    retour = 1;
	}

	return retour;
    }

    public Object clone() {
	LocalCell clone = null;
	try {
	    clone = (LocalCell) super.clone();
	} catch (CloneNotSupportedException cnse) {
	    cnse.printStackTrace(System.err);
	}

	return clone;
    }

    void SetRuleNumber(int newRN) {
	this.rulesnumber = newRN;
    }

    boolean gotRule() {
	return rulesnumber > -1;
    }

    boolean isAlive() {
	return States.isAlive(this.GetState());
    }
}
