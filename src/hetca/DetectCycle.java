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
class DetectCycle {

    static int[] ageGird = {0, 0, 0, 0, 0, 0, 0, 0};
    static byte[][][] savedGrid;
    final static int ISNOTALIVE = -1;
    private static final int NOTFOUND = -2;
    private static int[][] lastCycle;
    private static int[] regsiteredCycle;

    static String detectRecurence() {
	razRegistered();
	int iteratorCurrentGrid = addCurrentGird();
	String out = compageGird(iteratorCurrentGrid)+allCycle();
	return out;
    }

    private static int addCurrentGird() {
	return saveNewGird(findOldestGird());
    }

    private static int findOldestGird() {
	int minAge = -1;
	int girdIterator = -1;
	for (int i = 0; i < ageGird.length; i++) {
	    if (girdIterator == -1 || ageGird[i] < minAge) {
		minAge = ageGird[i];
		girdIterator = i;
	    }
	}
	return girdIterator;
    }

    private static int saveNewGird(int oldestGird) {
	ageGird[oldestGird] = CAGird.Generation;
	for (int x = 0; x < CAGird.CACells.length; x++) {
	    for (int y = 0; y < CAGird.CACells[0].length; y++) {
		savedGrid[oldestGird][x][y] = CAGird.CACells[x][y].GetState();
	    }
	}
	return oldestGird;
    }

    private static int compageGird(int iCurrentGrid) {
	int totalChanges = 0;
	for (int x = 0; x < CAGird.CACells.length; x++) {
	    for (int y = 0; y < CAGird.CACells[0].length; y++) {
		totalChanges += compareCells(x, y, iCurrentGrid);
	    }
	}
	
	return totalChanges;
    }

    private static int compareCells(int x, int y, int iCurrentGrid) {
	int newCycle = ISNOTALIVE;
	if (!States.isDecay(savedGrid[iCurrentGrid][x][y]) && !bothQuiescent(x,y,iCurrentGrid)) {
	    newCycle = findLastOccurence(iCurrentGrid, x, y);
	}

	return compareCycle(x, y, newCycle);
    }

    private static int findLastOccurence(int iCurrentGrid, int x, int y) {
	int generation = NOTFOUND;
	for (int i = 0; i < ageGird.length; i++) {
	    if (ageGird[i] < CAGird.Generation-1) {
		if (savedGrid[iCurrentGrid][x][y] == savedGrid[i][x][y] && generation < ageGird[i]) {
		    if(savedGrid[getPreviousIterator(iCurrentGrid)][x][y] == savedGrid[getPreviousIterator(i)][x][y]){
			    generation = ageGird[i];
		    }
		}

	    }
	}
	return CAGird.Generation-generation;
    }

    private static int compareCycle(int x, int y, int newCycle) {
	int ischange = 0;
	if (newCycle!= ISNOTALIVE && newCycle!= NOTFOUND && newCycle<8) {
	    if(newCycle!=lastCycle[x][y]){
		ischange = 1;
		lastCycle[x][y] = newCycle;
	    }
//	    System.out.println(newCycle);
	    registerCycle(newCycle-1);
	}
	return ischange;
    }

    static void initGird(int x, int y) {
	savedGrid = new byte[8][x][y];
	lastCycle = new int[x][y];
	regsiteredCycle = new int[7];
	for (byte[][] savedGrid1 : savedGrid) {
	    for (byte[] savedGrid2 : savedGrid1) {
		for (byte savedGrid3 : savedGrid2) {
		    savedGrid3 = 0;
		}
	    }
	}
	for (int[] savedGrid1 : lastCycle) {
	    for (int savedGrid2 : savedGrid1) {
		    savedGrid2 = 0;
	    }
	}
	for(int i=0; i < ageGird.length;i++){
	    ageGird[i] = 0;
	}
    }

    private static boolean bothQuiescent(int x, int y, int iCurrentGrid) {
	return States.isQuiescent(savedGrid[iCurrentGrid][x][y]) && States.isQuiescent(savedGrid[getPreviousIterator(iCurrentGrid)][x][y]);
    }

    private static int getPreviousIterator(int iCurrentGrid) {
	int iterator = 0;
	for(int i=0;i<ageGird.length;i++){
	    if(ageGird[iCurrentGrid]-1==ageGird[i]){
		iterator = i;
		i = ageGird.length;
	    }
	}
	return iterator;
    }

    private static void registerCycle(int newCycle) {
	regsiteredCycle[newCycle]++;
    }

    private static String allCycle() {
	String ret = "";
	for(int i=0;i<regsiteredCycle.length;i++){
	    ret += "\t"+regsiteredCycle[i];
	}
	return ret;
    }

    private static void razRegistered() {
	for(int i=0;i<regsiteredCycle.length;i++){
	    regsiteredCycle[i]=0;
	}
    }

}
