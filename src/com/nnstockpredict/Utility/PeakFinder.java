/*
 * Copyright 2012 Client Spectrum
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.Utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import sun.security.util.Length;

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

//PEAKFINDER Noise tolerant fast peak finding algorithm
//   INPUTS:
//       x0 - A real vector from the maxima will be found (required)
//       sel - The amount above surrounding data for a peak to be
//           identified (default = (max(x0)-min(x0))/4). Larger values mean
//           the algorithm is more selective in finding peaks.
//       thresh - A threshold value which peaks must be larger than to be
//           maxima or smaller than to be minima.
//       extrema - 1 if maxima are desired, -1 if minima are desired
//           (default = maxima, 1)
//   OUTPUTS:
//       peakLoc - The indicies of the identified peaks in x0
//       peakMag - The magnitude of the identified peaks
//
//   [peakLoc] = peakfinder(x0) returns the indicies of local maxima that
//       are at least 1/4 the range of the data above surrounding data.
//
//   [peakLoc] = peakfinder(x0,sel) returns the indicies of local maxima
//       that are at least sel above surrounding data.
//
//   [peakLoc] = peakfinder(x0,sel,thresh) returns the indicies of local
//       maxima that are at least sel above surrounding data and larger
//       (smaller) than thresh if you are finding maxima (minima).
//
//   [peakLoc] = peakfinder(x0,sel,thresh,extrema) returns the maxima of the
//       data if extrema > 0 and the minima of the data if extrema < 0
//
//   [peakLoc, peakMag] = peakfinder(x0,...) returns the indicies of the
//       local maxima as well as the magnitudes of those maxima
//
//   If called with no output the identified maxima will be plotted along
//       with the input data.
//
//   Note: If repeated values are found the first is identified as the peak
//
// Ex:
// t = 0:.0001:10;
// x = 12*sin(10*2*pi*t)-3*sin(.1*2*pi*t)+randn(1,numel(t));
// x(1250:1255) = max(x);
// peakfinder(x)
//
// Copyright Nathanael C. Yoder 2011 (nyoder@gmail.com)
// Perform error checking and set defaults if not passed in
// Rewrite by Davide Gazzè 12/06/2012
// Converted to Java by Ajay Bhaga 13/01/2013
    public static double max(List<Double> input) {
        double max = Double.MIN_VALUE;

        for (int i = 0; i < input.size(); i++) {
            max = Math.max(max, input.get(i));
        }

        return max;
    }

    public static double min(List<Double> input) {
        double min = Double.MAX_VALUE;

        for (int i = 0; i < input.size(); i++) {
            min = Math.min(min, input.get(i));
        }

        return min;
    }

    public static double max(double[] input) {
        double max = Double.MIN_VALUE;

        for (int i = 0; i < input.length; i++) {
            max = Math.max(max, input[i]);
        }

        return max;
    }

    public static double min(double[] input) {
        double min = Double.MAX_VALUE;

        for (int i = 0; i < input.length; i++) {
            min = Math.min(min, input[i]);
        }

        return min;
    }

    public static double[] diff(List<Double> input, int min, int max) {
        int len = input.size();
        if (min == -1) {
            min = 1;
        }

        if (max == -1) {
            max = len;
        }

        double[] diff_array = new double[input.size()];
        for (int i = min; i < max; i++) {
            diff_array[i - 1] = input.get(i) - input.get(i - 1);
        }

        return diff_array;
    }

    public static double[] diff(double[] input, int min, int max) {
        int len = input.length;
        if (min == -1) {
            min = 1;
        }

        if (max == -1) {
            max = len;
        }

        double[] diff_array = new double[input.length];
        for (int i = min; i < max; i++) {
            diff_array[i - 1] = input[i] - input[i - 1];
        }

        return diff_array;
    }

    public static int sign(double number) {
        return (number > 0.0) ? 1 : ((number < 0.0) ? -1 : 0);
    }

    public static int[] signArray(double[] input) {
        int len = input.length;
        int[] result = new int[input.length];
        for (int i = 0; i < len; i++) {
            result[i] = sign(input[i]);
        }
        return result;
    }

    public static Response maxIndex(List<Double> input) {
        double max = input.get(0);
        int index = 0;

        for (int i = 1; i < input.size(); i++) {
            if (input.get(i) > max) {
                max = input.get(i);
                index = i;
            }
        }

        Response response = new Response(max, index);
        return response;
    }

    public static int[] findChangeSign(double[] input) {
        int len = input.length - 1;
        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < len; i++) {
            if (input[i] * input[i + 1] <= 0.0) {
                list.add(i + 1);
            }
        }

        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }

        return result;
    }

    public static int[] intZeros(int num) {
        int[] result = new int[num];
        for (int i = 0; i < num; i++) {
            result[i] = 0;
        }

        return result;
    }

    public static double[] doubleZeros(int num) {
        double[] result = new double[num];
        for (int i = 0; i < num; i++) {
            result[i] = 0;
        }

        return result;
    }

    public static double[] mulArray(double[] input, int mul) {

        double[] result = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            result[i] = input[i] * mul;
        }

        return result;
    }

    public static Response peakFind(double[] input, double sel, int thresh, int extrema) {

        if (sel == -1.0) {
            sel = (max(input) - min(input)) / 4;
        }

        double[] x0;
        double[] dx0;

        // Make it so we are finding maxima regardless
        x0 = mulArray(input, extrema);
        // Adjust threshold according to extrema
        thresh = thresh * extrema;
        // Find derivative
        dx0 = diff(x0, -1, -1);

        // Find where the derivative changes sign
        int[] index_raw = findChangeSign(dx0);
        // Include endpoints in potential peaks and valleys
        // x = [x0(1);x0(ind);x0(end)];
        // ind = [1;ind;len0];
        int lenX0 = x0.length;

        List<Double> x = new ArrayList<Double>();
        List<Integer> index = new ArrayList<Integer>();

        index.add(0);
        x.add(x0[0]);

        for (int i = 0; i < index_raw.length; i++) {
            x.add(x0[index_raw[i]]);
            index.add(index_raw[i]);
        }

        x.add(x0[lenX0 - 1]);
        index.add(lenX0 - 1);

        // x only has the peaks, valleys, and endpoints
        int lenX = x.size();
        double minMag = min(x);

        int[] peakInds = null;
        double[] peakMags = null;

        // Function with peaks and valleys
        int ii = 0;

        if (lenX > 2) {
            // Set initial parameters for loop
            double tempMag = minMag;
            boolean foundPeak = false;
            double leftMin = minMag;
            //Deal with first point a little differently since tacked it on
            //Calculate the sign of the derivative since we taked the first point
            //on it does not neccessarily alternate like the rest.
            int[] signDx = signArray(diff(x, 1, 3));
            // The first point is larger or equal to the second
            if (signDx[0] <= 0) {
                // Want alternating signs
                ii = -1;
                if (signDx[0] == signDx[1]) {

                    for (int z = 1; z < lenX; z++) {
                        x.set(z - 1, x.get(z));
                        index.set(z - 1, index.get(z));
                    }

                    lenX--;
                    // Delete last value
                    x.remove(lenX);
                    index.remove(lenX);
                }
            } else {
                // First point is smaller than the second
                ii = 0;
                if (signDx[0] == signDx[1]) {

                    x.remove(x.size() - 1);
                    index.remove(index.size() - 1);
                    lenX--;
                }
            }
            // Preallocate max number of maxima
            int maxPeaks = (int) Math.ceil((double) lenX / (double) 2.0);
            int[] peakLoc = intZeros(maxPeaks);
            double[] peakMag = doubleZeros(maxPeaks);
            int cInd = 0;
            foundPeak = false;
            int tempLoc = 0;
            // Loop through extrema which should be peaks and then valleys
            while ((ii + 1) < lenX) {
                ii++;
                // This is a peak
                // Reset peak finding if we had a peak and the next peak is bigger
                // than the last or the left min was small enough to reset
                if (foundPeak) {
                    tempMag = minMag;
                    foundPeak = false;
                }
                // Make sure we don't iterate past the length of our vector
                if (ii == (lenX - 1)) {
                    // We assign the last point differently out of the loop
                    break;
                }
                // Found new peak that was lager than temp mag and selectivity larger than the minimum to its left
                if ((x.get(ii) > tempMag) && (x.get(ii) > (leftMin + sel))) {
                    tempLoc = ii;
                    tempMag = x.get(ii);
                }
                //$ii++;
                // Move onto the valley
                // Come down at least sel from peak
                //print((($foundPeak==true)?"true":"false") ." ".$tempMag." ".$x[$ii]."<br>");
                if ((!foundPeak) && (tempMag > (sel + x.get(ii)))) {
                    foundPeak = true; // We have found a peak
                    leftMin = x.get(ii);
                    peakLoc[cInd] = tempLoc; // Add peak to index
                    peakMag[cInd] = tempMag;
                    cInd++;
                } else if (x.get(ii) < leftMin) {
                    // New left minima
                    leftMin = x.get(ii);
                }
            }
            // Check end point
            if ((x.get(lenX - 1) > tempMag) && (x.get(lenX - 1) > leftMin + sel)) {
                peakLoc[cInd] = lenX - 1;
                peakMag[cInd] = x.get(lenX - 1);
                cInd++;
            } else if (!foundPeak && (tempMag > minMag)) {
                // Check if we still need to add the last point
                peakLoc[cInd] = tempLoc;
                peakMag[cInd] = tempMag;
                cInd++;
            }
            // Create output
            peakInds = new int[cInd];
            peakMags = new double[cInd];
            for (int i = 0; i < cInd; i++) {
                peakInds[i] = index.get(peakLoc[i]);
                peakMags[i] = peakMag[i];
            }
        } else {
            // This is a monotone function where an endpoint is the only peak
            Response response = maxIndex(x);
            peakInds = new int[1];
            peakMags = new double[1];
            peakMags[0] = response.getValue();
            int xInd = response.getIndex();

            if (peakMags[0] > minMag + sel) {
                peakInds[0] = index.get(xInd);
            }
        }
        // Apply threshold value.  Since always finding maxima it will always be larger than the thresh.
        if (thresh > 0) {

            int[] peakIndsNew = new int[thresh];
            double[] peakMagsNew = new double[thresh];

            for (int i = 0; i < thresh; i++) {
                if (peakMags[i] > thresh) {
                    peakIndsNew[i] = peakInds[i];
                    peakMagsNew[i] = peakMags[i];
                }
            }
            peakInds = peakIndsNew;
            peakMags = peakMagsNew;
        }
        // Change sign of data if was finding minima
        if (extrema < 0) {
            peakMags = mulArray(peakMags, -1);
            x0 = mulArray(x0, -1);
        }

        Response response = new Response(peakMags, peakInds);
        return response;

    }
}