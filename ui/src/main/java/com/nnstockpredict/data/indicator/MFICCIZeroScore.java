/*
 * MFI CCI Zero Score Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

public class MFICCIZeroScore extends StockIndicator {

    private final double UP_TREND = 1.0;
    private final double CONSOLIDATING_TREND = 0.0;
    private final double DOWN_TREND = -1.0;

    public MFICCIZeroScore(String name, int size) {
        super(name, size);
    }

    public void calculate(double[] mfiValue, int mfiBegIdx, double[] cciValue, int cciBegIdx, int maxBegIdx, int maxLength) {

        // Instantiate new values (this will store the score)
        value = new double[maxLength];

        // Translate the indices based on the max begin index
        int mfiI = maxBegIdx - mfiBegIdx;
        int cciI = maxBegIdx - cciBegIdx;

        for (int i = 0; i < maxLength; i++) {

            if ((i >= mfiI) && (i >= cciI)) {

                if ((mfiValue[i - mfiI] > 0.0) && (cciValue[i - cciI] > 0.0)) {
                    value[i] = UP_TREND;
                } else if ((mfiValue[i - mfiI] < 0.0) && (cciValue[i - cciI] < 0.0)) {
                    value[i] = DOWN_TREND;
                } else {
                    value[i] = CONSOLIDATING_TREND;
                }
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