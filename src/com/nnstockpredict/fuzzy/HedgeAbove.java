// Author: Ajay Bhaga
package com.nnstockpredict.fuzzy;

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
        double last = range[3];

        double d = range[0];

        // Iterate until we reach hit a fuzzy value of 1.0
        while (mf.fuzzify(d) < 1.0D) {
            d = d + 0.01D;            
            if (d > range[3]) {
                return -1.0D;
            }
        }
                
        System.out.println("mf = " + mf.getName());
        System.out.println("first x hitting max = " + d);
        System.out.println("x = " + x);
        System.out.println("value = " + value);
        
        if (x >= d + 1.0D) {
            System.out.println("hedged above to = " + 1.0D);
            return 1.0D;
        } else if (x >= d) {
            System.out.println("hedged above to = " + (x - d));
            return x - d;
        }
        System.out.println("hedged above to = " + 0.0D);
        return 0.0D;
    }
}
