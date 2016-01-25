/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hetca;

/**
 * 
 * @author David
 */
class SlidingMachine
{
  static void Slide(LocalCell[][] crrState, int sizX, int sizY, boolean isShadow)
  {
    byte[] allCnt = new byte[9];
    int[] lurd = new int[4];

    for (int i = 0; i < sizX; i++)
    {
      if (i > 0) {
        lurd[0] = (i - 1);
      }
      else {
        lurd[0] = (sizX - 1);
      }
      if (i < sizX - 1) {
        lurd[2] = (i + 1);
      }
      else {
        lurd[2] = 0;
      }
      for (int j = 0; j < sizY; j++) {

        if (j > 0) {
          lurd[1] = (j - 1);
        }
        else {
          lurd[1] = (sizY - 1);
        }
        if (j < sizY - 1) {
          lurd[3] = (j + 1);
        }
        else {
          lurd[3] = 0;
        }
        int iCnt = 0;
        int Cntbonus = 0;

        allCnt[2] = crrState[i][lurd[1]].GetStateWthAudoDecay();
        allCnt[3] = crrState[i][lurd[3]].GetStateWthAudoDecay();
        allCnt[4] = crrState[lurd[0]][j].GetStateWthAudoDecay();
        allCnt[1] = crrState[lurd[2]][j].GetStateWthAudoDecay();

        allCnt[0] = crrState[i][j].GetStateWthAudoDecay();

        allCnt[5] = crrState[lurd[0]][lurd[1]].GetStateWthAudoDecay();
        allCnt[6] = crrState[lurd[2]][lurd[3]].GetStateWthAudoDecay();
        allCnt[7] = crrState[lurd[2]][lurd[1]].GetStateWthAudoDecay();
        allCnt[8] = crrState[lurd[0]][lurd[3]].GetStateWthAudoDecay();

        int sum;
        if (States.isAlive(allCnt[0])) {
          sum = crrState[i][j].GetCellNewState(allCnt);
        }
        else {
          sum = allCnt[0];
        }
        int pow = 1;
        for (int k = 1; k < 9; k++) {
          pow *= k;
          sum += pow * allCnt[k];
        }
	
        if (isShadow) {
	    
          Patern.TryToAddS(sum, i, j);
        }
        else
        {
	    
          Patern.TryToAdd(sum, i, j);
        }
	
      }
    }
  }
}
