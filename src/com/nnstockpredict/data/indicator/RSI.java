/*
 * RSI Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.tictactec.ta.lib.Core;
import java.util.Random;

public class RSI extends StockIndicator {

    public RSI(String name, int size) {
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
        setMin(0);
        setMax(100);

        // Calculate RSI
        checkRange(core.rsi(0, inputClose.length - 1, inputClose, 14, begIdx, length, value));

    }
}
