/*
 * Stochastic Indicator K% (fast)
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;

public class StochF extends StockIndicator {

    public StochF(String name, int size) {
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

        // Set bounds
        setMin(-20);
        setMax(120);

        // Calculate Stoch
        double[] outFastK = new double[value.length];
        double[] outFastD = new double[value.length];
        checkRange(core.stochF(0, inputClose.length - 1, inputHigh, inputLow, inputClose, 10, 3, MAType.Sma, begIdx, length, outFastK, outFastD));
        value = outFastD;
    }
}
