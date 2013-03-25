// Author: Ajay Bhaga
package com.bhaga.pinetree.nn.fuzzy;

/**
 * This hedge is an implementation of fuzzy "above"
 */
public class HedgeAbove extends Hedge {

    /**
     * getName method for "above"
     */
    public java.lang.String getName() {
        return "above";
    }

    /**
     * hedgeIt method for "above"
     */
    public double hedgeIt(double value, MembershipFunction mf, double x) {
        double[] range = mf.getRange();

        double d = range[0];

        // Iterate until we reach hit a fuzzy value of 1.0
        while (mf.fuzzify(d) < 1.0D) {
            d = d + 0.01D;            
            if (d > range[3]) {
                return -1.0D;
            }
        }
                        
        if (x >= d + 1.0D) {
            return 1.0D;
        } else if (x >= d) {
            return x - d;
        }
        return 0.0D;
    }
}
