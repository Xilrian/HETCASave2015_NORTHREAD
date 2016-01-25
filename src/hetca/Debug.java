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
class Debug {
    
  
      void checkintegrity(LocalCell[][] crrState, int sizX, int sizY, String step) {
        boolean warning = false;
        int compteur = 0;
        int compteur2 = 0;
        for (int i = 0; i < sizX; i++) {
            for (int j = 0; j < sizY; j++) {
                if (States.isAlive(crrState[i][j].GetState()) && (crrState[i][j].rulesnumber < 0)) {
                    warning = true;
                    compteur++;
                } else {
                    compteur2++;
                }
            }
        }
        if (warning) {
            System.out.println("warning !!! => total rules :" + LocalRule.totalRules + "/" + compteur2 + " step :" + step + ", compteur : " + compteur + ", Cycle : " + CAGird.Generation);
        } else {
            System.out.println("No warning !!! => total rules :" + LocalRule.totalRules + "/" + compteur2 + " step :" + step + ", compteur : " + compteur + ", Cycle : " + CAGird.Generation);
        }
    }

    static void checkCycle(String message) {
        if (CAGird.Generation > 1) {
            System.out.println(message);
        }
    }
    
}
