/*
 * Exponential 5-day moving average Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.tictactec.ta.lib.Core;

public class EMA5 extends StockIndicator {

    public EMA5(String name, int size) {
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

        // Calculate EMA 5
        checkRange(core.ema(0, inputClose.length - 1, inputClose, 5, begIdx, length, value));
    }
}
