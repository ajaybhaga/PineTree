/*
 * MACD Peak Score Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.nnstockpredict.Utility.PeakFinder;
import com.nnstockpredict.Utility.Response;
import java.util.List;

public class MACDPeakScore extends StockIndicator {

    private final double UP_TREND = 1.0;
    private final double CONSOLIDATING_TREND = 0.0;
    private final double DOWN_TREND = -1.0;

    public MACDPeakScore(String name, int size) {
        super(name, size);
    }

    public void calculate2(double[] inputClose, double[] macdValue, int macdBegIdx, int maxBegIdx, int maxLength) {

        System.out.println("Detecting peaks in MACD values...");
        // Detect all peaks
        List[] arrayOfLists = PeakFinder.detectPeaks(macdValue, 0.5);

        //Response response = PeakFinder.peakFind(macdValue, -1.0, 0, 1);
        Response response = PeakFinder.peakFind(macdValue, 0.02, 0, 1);

        System.out.println("PEAK FINDER:");
        for (int i = 0; i < response.getIndexArray().length; i++) {
            System.out.println("Peak Find: Index[" + i + "] = " + response.getIndexArray()[i]);
            System.out.println("Peak Find: Value[" + i + "] = " + response.getValueArray()[i]);
        }

        // Maxima list
        List<Integer> maxIdxList = arrayOfLists[PeakFinder.MAX_LIST];
        // Minima list
        List<Integer> minIdxList = arrayOfLists[PeakFinder.MIN_LIST];

        System.out.println("Number of max peaks: " + maxIdxList.size());
        System.out.println("Number of min peaks: " + minIdxList.size());

        // Instantiate new values (this will store the score)
        value = new double[maxLength];

        // Instantiate raw values (not all of these will be used)
        double[] rawValue = new double[inputClose.length];

        // Translate the indices based on the max begin index
        int macdI = maxBegIdx - macdBegIdx;

        for (int i = -1; i < maxIdxList.size(); i++) {

            int currPeakIdx;
            double currPeak;
            double currPrice;
            double trend;

            // The first iteration should start before the peak
            if (i == -1) {
                int nextPeakIdx = maxIdxList.get(0);
                double nextPeak = macdValue[nextPeakIdx];
                double nextPrice = inputClose[nextPeakIdx];

                // Fill in scores up to peak index
                for (int j = 0; j <= nextPeakIdx; j++) {

                    currPeak = macdValue[j];
                    currPrice = inputClose[j];

                    if ((currPeak < nextPeak) && (currPrice < nextPrice)) {
                        // Up trend
                        System.out.println("Up trend detected.");
                        trend = UP_TREND;
                    } else if ((currPeak > nextPeak) && (currPrice > nextPrice)) {
                        // Down trend
                        System.out.println("Down trend detected.");
                        trend = DOWN_TREND;
                    } else {
                        // Consolidating trend
                        System.out.println("Consolidating detected.");
                        trend = CONSOLIDATING_TREND;
                    }

                    rawValue[j] = trend;
                }

            } else {
                // Within peaks
                currPeakIdx = maxIdxList.get(i);
                currPeak = macdValue[currPeakIdx];
                currPrice = inputClose[currPeakIdx];

                if (i + 1 < maxIdxList.size()) {
                    int nextPeakIdx = maxIdxList.get(i + 1);
                    double nextPeak = macdValue[nextPeakIdx];
                    double nextPrice = inputClose[nextPeakIdx];

                    if ((currPeak < nextPeak) && (currPrice < nextPrice)) {
                        // Up trend
                        System.out.println("Up trend detected.");
                        trend = UP_TREND;
                    } else if ((currPeak > nextPeak) && (currPrice > nextPrice)) {
                        // Down trend
                        System.out.println("Down trend detected.");
                        trend = DOWN_TREND;
                    } else {
                        // Consolidating trend
                        System.out.println("Consolidating detected.");
                        trend = CONSOLIDATING_TREND;
                    }

                    // Fill in scores up to peak index
                    for (int j = currPeakIdx; j <= nextPeakIdx; j++) {
                        rawValue[j] = trend;
                        //value[j] = (nextPeak - macdValue[j]);
                        //System.out.println("Calculated score and stored: " + value[j]);
                    }

                    System.out.println("currPeakIdx: " + currPeakIdx);
                    System.out.println("nextPeakIdx: " + nextPeakIdx);

                } else {
                    // No more peaks

                    // Fill in scores from the peak index to the end of the set
                    for (int j = currPeakIdx; j < macdValue.length; j++) {

                        // Calculate the trend based off of the MACD value

                        if ((currPeak < macdValue[j]) && (currPrice < inputClose[j])) {
                            // Up trend
                            System.out.println("Up trend detected.");
                            trend = UP_TREND;
                        } else if ((currPeak > macdValue[j]) && (currPrice > inputClose[j])) {
                            // Down trend
                            System.out.println("Down trend detected.");
                            trend = DOWN_TREND;
                        } else {
                            // Consolidating trend
                            System.out.println("Consolidation detected.");
                            trend = CONSOLIDATING_TREND;
                        }

                        rawValue[j] = trend;
                    }
                }
            }
        }

        for (int i = 0; i < maxLength; i++) {
            value[i] = 0;
        }

        for (int i = 0; i < response.getIndexArray().length; i++) {
            //       System.out.println("Peak Find: Index[" + i + "] = " + response.getIndexArray()[i]);
            //        System.out.println("Peak Find: Value[" + i + "] = " + response.getValueArray()[i]);
            value[response.getIndexArray()[i]] = response.getValueArray()[i];
        }

        // We only copy over starting from the valid MACD values
        /*for (int i = 0; i < maxLength; i++) {
         value[i] = rawValue[macdI+i];
         } */

        // Manually set the begin and length values
        begIdx.value = 0;
        length.value = value.length;
    }

    public void calculate(double[] inputClose, double[] macdValue, int macdBegIdx, int maxBegIdx, int maxLength) {

        System.out.println("Detecting peaks in MACD values...");
        // Detect all peaks
        //Response response = PeakFinder.peakFind(macdValue, -1.0, 0, 1);
        Response response = PeakFinder.peakFind(macdValue, 0.005, 0, 1);

        System.out.println("PEAK FINDER:");
        for (int i = 0; i < response.getIndexArray().length; i++) {
            System.out.println("Peak Find: Index[" + i + "] = " + response.getIndexArray()[i]);
            System.out.println("Peak Find: Value[" + i + "] = " + response.getValueArray()[i]);
        }

        System.out.println("MACDPeak: maxLength {" + maxLength + "}, response.getIndexArray().length {" + response.getIndexArray().length + "}, response.getValueArray().length {" + response.getValueArray().length + "}");
        // Instantiate new values (this will store the score)
        value = new double[maxLength];

        for (int i = 0; i < response.getIndexArray().length; i++) {
            //System.out.println("Peak Find: Index[" + i + "] = " + response.getIndexArray()[i]);
            //System.out.println("Peak Find: Value[" + i + "] = " + response.getValueArray()[i]);

            if (response.getIndexArray()[i] < maxLength) {
                value[response.getIndexArray()[i]] = response.getValueArray()[i];
            }
        }

        // Manually set the begin and length values
        begIdx.value = 0;
        length.value = value.length;
    }

    @Override
    public void calculate(double[] inputHigh, double[] inputLow, double[] inputVolume, double[] inputOpen, double[] inputClose) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}