/*
 * Exponential 10-day moving average Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.tictactec.ta.lib.Core;

public class EMA10 extends StockIndicator {

    public EMA10(String name, int size) {
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

        // Calculate EMA 10
        checkRange(core.ema(0, inputClose.length - 1, inputClose, 10, begIdx, length, value));
    }
}
