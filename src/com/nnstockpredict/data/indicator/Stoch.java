/*
 * Stochastic Indicator D% (slow)
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;

public class Stoch extends StockIndicator {

    public Stoch(String name, int size) {
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
        double[] outSlowK = new double[value.length];
        double[] outSlowD = new double[value.length];
        checkRange(core.stoch(0, inputClose.length - 1, inputHigh, inputLow, inputClose, 10, 3, MAType.Sma, 3, MAType.Sma, begIdx, length, outSlowK, outSlowD));
        value = outSlowD;
    }
}
