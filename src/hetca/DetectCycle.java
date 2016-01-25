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

    private static int[] ageGird = {0, 0, 0, 0, 0, 0, 0};
    private static byte[][][] savedGrid;
    final static int ISNOTALIVE = -1;
    private static int NOTFOUND = -2;
    private static int[][] lastCycle;

    static String detectRecurence() {
	int iteratorCurrentGrid = addCurrentGird();
	return "" + compageGird(iteratorCurrentGrid);
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
	if (States.isAlive(savedGrid[iCurrentGrid][x][y])) {
	    newCycle = findLastOccurence(iCurrentGrid, x, y);
	}

	return compareCycle(x, y, newCycle);
    }

    private static int findLastOccurence(int iCurrentGrid, int x, int y) {
	int generation = NOTFOUND;
	for (int i = 0; i < ageGird.length; i++) {
	    if (ageGird[i] != CAGird.Generation) {
		if (savedGrid[iCurrentGrid][x][y] == savedGrid[i][x][y] && generation < ageGird[i]) {
		    generation = ageGird[i];
		}

	    }
	}
	return generation;
    }

    private static int compareCycle(int x, int y, int newCycle) {
	int ischange = 0;
	if (lastCycle[x][y] != newCycle
		&& (lastCycle[x][y] != ISNOTALIVE && newCycle != ISNOTALIVE)
		&& (lastCycle[x][y] != NOTFOUND && newCycle != NOTFOUND)) {
	    ischange = 1;
	}
	lastCycle[x][y] = newCycle;
	return ischange;
    }

    static void initGird(int x, int y) {
	savedGrid = new byte[7][x][y];
	lastCycle = new int[x][y];
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
    }

}
