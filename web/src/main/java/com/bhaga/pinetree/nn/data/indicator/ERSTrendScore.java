/*
 * ERS Trend Score Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

public class ERSTrendScore extends StockIndicator {

    private final double UP_TREND = 1.0;
    private final double CONSOLIDATING_TREND = 0.0;
    private final double DOWN_TREND = -1.0;
    private final double ADX_QUALIFY_SIGNAL = 20.0;

    public ERSTrendScore(String name, int size) {
        super(name, size);
    }

    public void calculate(double[] ema5Value, int ema5BegIdx, double[] ema10Value, int ema10BegIdx, double[] rsiValue, int rsiBegIdx, double[] stochKValue, int stochKBegIdx, double[] stochDValue, int stochDBegIdx, int maxBegIdx, int maxLength) {

        // Instantiate new values (this will store the score)
        value = new double[maxLength];
        
        // Translate the indices based on the max begin index
        int ema5I = maxBegIdx - ema5BegIdx;
        int ema10I = maxBegIdx - ema10BegIdx;
        int rsiI = maxBegIdx - rsiBegIdx;
        int stochKI = maxBegIdx - stochKBegIdx;
        int stochDI = maxBegIdx - stochDBegIdx;   

        for (int i = 0; i < maxLength; i++) {

            if ((ema5Value[ema5I+i] >= ema10Value[ema10I+i]) && (rsiValue[rsiI+i] > 50.0) && (stochKValue[stochKI+i] > 79.0) && (stochDValue[stochDI+i] > 79.0)) {
                value[i] = UP_TREND;
            } else if ((ema5Value[ema5I+i] <= ema10Value[ema10I+i]) && (rsiValue[rsiI+i] < 50.0) && (stochKValue[stochKI+i] < 19.0) && (stochDValue[stochDI+i] < 19.0)) {
                value[i] = DOWN_TREND;                              
            } else {
                value[i] = CONSOLIDATING_TREND;
            }    
                
            /*
                System.out.println("ema5Value["+(ema5I+i)+"]=" + ema5Value[ema5I+i]);
                System.out.println("ema10Value["+(ema10I+i)+"]=" + ema10Value[ema10I+i]);
                System.out.println("rsiValue["+(rsiI+i)+"]=" + rsiValue[rsiI+i]);
                System.out.println("stochKValue["+(stochKI+i)+"]=" + stochKValue[stochKI+i]);
                System.out.println("stochDValue["+(stochDI+i)+"]=" + stochDValue[stochDI+i]);
                System.out.println("value["+i+"]=" + value[i]);
            */
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