/*
 * MACD Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.tictactec.ta.lib.Core;

public class MACD extends StockIndicator {

    public MACD(String name, int size) {
        super(name, size);
    }

    @Override
    public void calculate(
            double[] inputHigh,
            double[] inputLow,
            double[] inputVolume,
            double[] inputOpen,
            double[] inputClose) {

        // Instantiate core of TA-lib
        Core core = new Core();

        // Calculate MACD
        double[] macdSignal = new double[value.length];
        double[] macdHist = new double[value.length];
        checkRange(core.macd(0, inputClose.length - 1, inputClose, 12, 26, 9, begIdx, length, value, macdSignal, macdHist));
        
        System.out.println("MACD SIZE: " + length.value);
    }
}