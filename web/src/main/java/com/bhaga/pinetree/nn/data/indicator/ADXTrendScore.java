/*
 * ADX Trend Score Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

public class ADXTrendScore extends StockIndicator {

    private final double UP_TREND = 1.0;
    private final double CONSOLIDATING_TREND = 0.0;
    private final double DOWN_TREND = -1.0;
    private final double ADX_QUALIFY_SIGNAL = 20.0;

    public ADXTrendScore(String name, int size) {
        super(name, size);
    }

    public void calculate(double[] adxValue, int adxBegIdx, double[] diPlus, int diPlusBegIdx, double[] diMinus, int diMinusBegIdx, int maxBegIdx, int maxLength) {

        // Instantiate new values (this will store the score)
        value = new double[maxLength];
        
        // Translate the indices based on the max begin index
        int adxI = maxBegIdx - adxBegIdx;
        int diPlusI = maxBegIdx - diPlusBegIdx;
        int diMinusI = maxBegIdx - diMinusBegIdx;

        for (int i = 0; i < maxLength; i++) {
            if ((adxValue[adxI+i] >= ADX_QUALIFY_SIGNAL) && (diPlus[diPlusI+i] > diMinus[diMinusI+i])) {
                value[i] = UP_TREND;
            } else if ((adxValue[adxI+i] >= ADX_QUALIFY_SIGNAL) && (diPlus[diPlusI+i] < diMinus[diMinusI+i])) {
                value[i] = DOWN_TREND;
            } else if (adxValue[adxI+i] < ADX_QUALIFY_SIGNAL) {
                value[i] = CONSOLIDATING_TREND;
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