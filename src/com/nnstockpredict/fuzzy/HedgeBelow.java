// Author: Ajay Bhaga
package com.nnstockpredict.fuzzy;

/**
 * This hedge is an implementation of fuzzy "below"
 */
public class HedgeBelow extends Hedge {

    /**
     * getName method for "below"
     */
    public java.lang.String getName() {
        return "below";
    }

    /**
     * hedgeIt method for "below"
     */
    public double hedgeIt(double value, MembershipFunction mf, double x) {
        double[] range = mf.getRange();

        double d = range[3];

        // Iterate until we reach hit a fuzzy value of 1.0
        while (mf.fuzzify(d) < 1.0D) {
            d = d - 0.01D;
            if (d < range[0]) {
                return -1.0D;
            }
        }

        System.out.println("mf = " + mf.getName());
        System.out.println("first x hitting max = " + d);
        System.out.println("x = " + x);
        System.out.println("value = " + value);

        if (x <= d - 1.0D) {
            System.out.println("hedged above to = " + 1.0D);
            return 1.0D;
        } else if (x <= d) {
            System.out.println("hedged above to = " + (d - x));
            return d - x;
        }
        System.out.println("hedged above to = " + 0.0D);
        return 0.0D;
    }
}
