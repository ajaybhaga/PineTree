/*
 * Copyright 2012 Client Spectrum
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.Utility;

import java.util.ArrayList;
import java.util.List;

public class PeakFinder {
    
    public static final int MAX_LIST = 0;
    public static final int MIN_LIST = 1;
    
    /*
     * Find the local maxima and minima ("peaks") in the array value.
     *
     * A point is considered a maximum peak if it has the maximal value, and was
     * preceded (to the left) by a value lower by delta.
     */
    public static List<Integer>[] detectPeaks(double[] value, double delta) {

        // Instantiate array of lists
        List[] arrayOfLists = new List[2];

        // Maxima list
        List maxIdxList = new ArrayList<Integer>(100);
        // Minima list
        List minIdxList = new ArrayList<Integer>(100);

        double mn = Double.MAX_VALUE;
        double mx = Double.MIN_VALUE;

        boolean lookForMax = true;

        for (int i = 0; i < value.length; i++) {
            if (value[i] > mx) {
                mx = value[i];
            }

            if (value[i] < mn) {
                mn = value[i];
            }


            if (lookForMax) {
                if (value[i] < mx - delta) {
                    maxIdxList.add(i);
                    mn = value[i];
                    lookForMax = false;
                }
            } else {
                if (value[i] > mn + delta) {
                    minIdxList.add(i);
                    mx = value[i];
                    lookForMax = true;
                }
            }
        }

        arrayOfLists[MAX_LIST] = maxIdxList;
        arrayOfLists[MIN_LIST] = minIdxList;

        // Return array of lists
        return arrayOfLists;
    }
}
