/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hetca;

import java.math.BigInteger;
import java.util.Arrays;

/**
 *
 * @author David
 */
public class ProcessStatsGeneric {
    public static double [] getErrorMedian(double[] AllResult, double maxError) {
	double [] AllResultChances = new double [AllResult.length];

	Arrays.sort(AllResult);
	
	for(int k=0;k<AllResultChances.length;k++){
	
	    AllResultChances[k]=1.0/(BigInteger.valueOf((long) Math.pow(2, k)).multiply(BigInteger.valueOf((long) Math.pow(2, AllResult.length-k))).divide(cnk(AllResult.length,k)).doubleValue());

	}
	double error = 0;
	int e;
	for(e=0;error<maxError;e++){
	    error += AllResultChances[e]*2;
	}
	double [] results = {
	    AllResult[e-1],
	    AllResult[AllResult.length-e]
	};
	return results;
    }
    
    public static double getMedian(double[] numArray){
	Arrays.sort(numArray);
	double median;
	if (numArray.length % 2 == 0)
	    median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
	else {
	    
	    median = (double) numArray[numArray.length/2];
	}
	return median;
    }
    
    
    public static BigInteger cnk(int n, int k)
    {
	    BigInteger fenzi = new BigInteger("1");
	    BigInteger fenmu = new BigInteger("1");
	    for(int i=n-k+1; i <= n; i++){
		    String s = Integer.toString(i);
		    BigInteger stobig = new BigInteger(s);
		    fenzi = fenzi.multiply(stobig);
	    }
	    for(int j=1; j <= k; j++){
		    String ss = Integer.toString(j);
		    BigInteger stobig2 = new BigInteger(ss);
		    fenmu = fenmu.multiply(stobig2);
	    }
	    BigInteger result = fenzi.divide(fenmu);
	    return result;
    }
}
