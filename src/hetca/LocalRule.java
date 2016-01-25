package hetca;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Stack;

public class LocalRule
	implements Cloneable {

    static int numAdditionalRegisters;
    final static int listSize = 1000000;
    static LocalRule[] Fulllist = new LocalRule[listSize];


    private CALGPIndividual RulesList;
    private int parentID = -1;
    public int frequenceNormal = 1;
    public int frequenceShadow = 0;
    private int Cycle = 0;
    private int distance = 1;
    static Stack liste = new Stack();
    static Stack listeOKID = new Stack();
    static int totalRules = 0;
    static int totalRulesShadow = 0;
    private int distFromO = 0;
    private short CorX = 0;
    private short CorY = 0;
    private boolean isMax = false;

    public LocalRule() {
	this.RulesList = new CALGPIndividual(9, LocalCell.getNumStates(), numAdditionalRegisters);

	totalRules += 1;
    }

    public LocalRule(String genotype) {
	this.RulesList = new CALGPIndividual(genotype);

	totalRules += 1;
    }

    public int getDistance() {
	return this.distance;
    }

    public LocalRule(CALGPIndividual rule, int parent, int distFromOr) {
	this.RulesList = rule;
	this.parentID = parent;
	this.Cycle = CAGird.Generation;

	totalRules += 1;
	this.distFromO = (distFromOr + 1);
    }

    static int addNewRule() {
	int lastAddition = ((Integer) listeOKID.pop());
	Fulllist[lastAddition] = new LocalRule();
	return lastAddition;

    }

    public byte GetNewStateFromRule(byte[] index) {
	return (byte) (int) this.RulesList.apply(index);
    }

    public void copyRules(LocalRule rules) {
	this.RulesList = rules.RulesList.clone();
    }

    int randomise(int id) {
	double random = Math.random();
	if (random < 0.8D) {
	    random = 0.05D;
	} else if (random < 0.995D) {
	    random = 0.1D;
	} else {
	    random = 0.5D;
	}
	int lastAddition = ((Integer) listeOKID.pop());
	Fulllist[lastAddition] = new LocalRule((CALGPIndividual) this.RulesList.mutate(random), id, this.distFromO);
	return lastAddition;
    }

    static void RazCounterGenotypeList() {
	for (int key = 0; key < listSize; key++) {
	    LocalRule testRule = Fulllist[key];
	    if ((testRule != null) && ((testRule.frequenceNormal > 0) || (testRule.frequenceShadow > 0))) {
		Fulllist[key].frequenceNormal = 0;
		Fulllist[key].frequenceShadow = 0;
		Fulllist[key].CorX = -1;
		Fulllist[key].isMax = false;
	    }
	}
    }

    static void CleanTable(int compteur, boolean ishadow) {
	if (!ishadow) {
	    totalRules = compteur;
	} else {
	    totalRulesShadow = compteur;
	}
	for (int key = 0; key < listSize; key++) {
	    LocalRule testRule = Fulllist[key];
	    if ((testRule != null) && (testRule.frequenceShadow == 0) && (testRule.frequenceNormal == 0)) {
		if (CAGird.Generation > 15) {
		    for (int roxKey = 0; roxKey < listSize; roxKey++) {
			LocalRule Rule = Fulllist[roxKey];
			if ((Rule != null) && (Rule.frequenceShadow > 0) && (Rule.frequenceNormal > 0)
				&& (Rule.parentID == key)) {
			    if (testRule.parentID != -1) {
				Rule.parentID = testRule.parentID;
			    } else {
				Rule.parentID = (-key);
			    }

			    Rule.distance += testRule.distance;
			}

		    }

		}

		listeOKID.push(key);
		Fulllist[key] = null;
	    }
	}
    }

    static void DisplayTable(String path, String pathGenome) throws IOException {
//	System.out.println("DisplayTable");
	String returnval = "";
	FileWriter lu = new FileWriter(path + ".txt");
	BufferedWriter out = new BufferedWriter(lu);
	out.write("Nrm Total number of rules : " + totalRules);
	out.newLine();
	int Sactivity = 0;
	int activity = 0;
	int NewSActiv = 0;
	int NewActiv = 0;
	String stringMax;
	int k = 0;
	for (int key = 0; key < listSize; key++) {

	    LocalRule testRule = Fulllist[key];
	    if ((testRule != null) && ((testRule.frequenceShadow > 0) || (testRule.frequenceNormal > 0))) {
//		System.out.println("save Genome");
		int pCycle = 0;
		if ((testRule.parentID > 0) && (Fulllist[testRule.parentID] != null)) {
		    pCycle = Fulllist[testRule.parentID].Cycle;
		}
		if (testRule.frequenceNormal > 0) {

		    if (testRule.isNew()) {
			NewActiv++;
		    }
		    stringMax = "";
		    if (testRule.isMax) {
			stringMax = "**********";
			FileWriter saveGen = new FileWriter(pathGenome + "-" + k + ".txt");
			BufferedWriter SGout = new BufferedWriter(saveGen);
			SGout.write(testRule.RulesList.toString());
			SGout.close();
			k++;
		    }
		    activity += (CAGird.Generation - testRule.Cycle) / 150;
		    out.write(stringMax + "ID : C" + testRule.Cycle + "I" + key + ", Parent ID : C" + pCycle + "I" + testRule.parentID + ", FrequenceNormal : " + testRule.frequenceNormal + ", FrequenceShadow : " + testRule.frequenceShadow + ", Distance : " + testRule.distance + ", Creation : " + testRule.Cycle + ", Nbr Muta From origin : " + testRule.distFromO + ", Longueur genome : " + testRule.RulesList.getTotalSize() + ", Coordinate : " + testRule.CorX + "x" + testRule.CorY
			    + ", Activity : " + ((CAGird.Generation - testRule.Cycle) / 150));

		    out.newLine();
		}

	    }
	}
	out.write("Diversity : " + totalRules + ", Bedau Mean cumulative evolutionary activity : " + ((double) Sactivity) / ((double) totalRules) + ", Bedau new evolutionary activity : " + ((double) NewActiv) / ((double) totalRules));
	out.close();
    }

    static void initialize() {
	if(Parameters.isCompare){
	    initializeLoadCompare("genotype/tr","genotype/st");
	}
	else{
	    initializeRandom();
	}
    }
    
    static void initialize(String pathGenotype) {
	loadNewRule(pathGenotype, 0);
    }
    

    static void initializeRandom() {
	totalRules = 0;
	listeOKID = new Stack();
	liste = new Stack();
	for (int key = 0; key < listSize; key++) {
	    listeOKID.push(key);
	}
    }
    

    static void initializeLoadCompare(String genomePath1, String GenomePath2) {
	loadNewRule(genomePath1, 0);
	loadNewRule(GenomePath2, 1);
    }

    void increaseNumNormal() {
	this.frequenceNormal += 1;
    }

    int increaseNumShadow() {
	this.frequenceShadow += 1;
	return this.frequenceShadow;
    }

    void setXY(int i, int j) {
	this.CorX = ((short) i);
	this.CorY = ((short) j);
    }

    @Override
    public LocalRule clone() throws CloneNotSupportedException {
	LocalRule o = null;
	try {
	    o = (LocalRule) super.clone();
	    o.RulesList = this.RulesList.clone();
	} catch (CloneNotSupportedException cnse) {
	    cnse.printStackTrace(System.err);
	}

	return o;
    }

    void unsetMax() {
	this.isMax = false;
    }

    void setMax() {
	this.isMax = true;
    }

    private boolean isNew() {
	boolean ret = false;
	if (this.Cycle > CAGird.Generation + 150) {
	    ret = true;
	}
	return ret;
    }

    static int loadNewRule(String genome, int num) {
	String StrinGen = "";
	try {
	    BufferedInputStream in = new BufferedInputStream(new FileInputStream(genome ));
	    StringWriter out = new StringWriter();
	    int b;
	    while ((b = in.read()) != -1) {
		out.write(b);
	    }
	    out.flush();
	    out.close();
	    in.close();
	    StrinGen = out.toString();
	} catch (IOException ie) {
	    ie.printStackTrace();
	}
	Fulllist[num] = new LocalRule(StrinGen);
	System.out.println(num+"\n  \n "+StrinGen);
	return num;
    }
}
