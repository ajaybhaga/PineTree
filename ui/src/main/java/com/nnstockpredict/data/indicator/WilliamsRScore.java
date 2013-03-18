/*
 * Williams R Score Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

public class WilliamsRScore extends StockIndicator {

    private final double STRONG_UP_TREND = 1.0;
    private final double UP_TREND = 0.5;
    private final double CONSOLIDATING_TREND = 0.0;
    private final double DOWN_TREND = -0.5;
    private final double STRONG_DOWN_TREND = -1.0;

    public WilliamsRScore(String name, int size) {
        super(name, size);
    }

    public void calculate(double[] willRValue, int willRBegIdx, int maxBegIdx, int maxLength) {      
        
        // Instantiate new values (this will store the score)
        value = new double[maxLength];
                
        // Translate the indices based on the max begin index
        int willRI = maxBegIdx - willRBegIdx;
        
        for (int i = 0; i < maxLength; i++) {
            if (willRValue[willRI+i] > -50.0) {
                value[i] = UP_TREND;

                if (willRValue[willRI+i] >= -25.0) {
                    value[i] = STRONG_UP_TREND;
                }

            } else if (willRValue[willRI+i] < -50.0) {
                value[i] = DOWN_TREND;

                if (willRValue[willRI+i] <= -75.0) {
                    value[i] = STRONG_DOWN_TREND;
                }

            } else {
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