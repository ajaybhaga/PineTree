/*
 * MACD Zero Score Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

public class MACDZeroScore extends StockIndicator {

    private final double UP_TREND = 1.0;
    private final double CONSOLIDATING_TREND = 0.0;
    private final double DOWN_TREND = -1.0;

    public MACDZeroScore(String name, int size) {
        super(name, size);
    }

    public void calculate(double[] macdValue, int macdBegIdx, int maxBegIdx, int maxLength) {

        // Instantiate new values (this will store the score)
        value = new double[maxLength];
        
        // Translate the indices based on the max begin index
        int macdI = maxBegIdx - macdBegIdx;
        
        for (int i = 0; i < maxLength; i++) {
            if (macdValue[macdI+i] > 0.0) {
                value[i] = UP_TREND;
            } else if (macdValue[macdI+i] < 0.0) {
                value[i] = DOWN_TREND;
            } else {
                value[i] = CONSOLIDATING_TREND;
            }            
        }

        // Manually set the begin and length values
        begIdx.value = 0;
        length.value = value.length;
        
        System.out.println("MACDZERO SIZE: " + value.length);
    }

    @Override
    public void calculate(double[] inputHigh, double[] inputLow, double[] inputVolume, double[] inputOpen, double[] inputClose) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}