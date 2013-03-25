/*
 * Exponential 30-day moving average Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

import com.tictactec.ta.lib.Core;

public class EMA30 extends StockIndicator {

    public EMA30(String name, int size) {
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

        // Calculate EMA 30
        checkRange(core.ema(0, inputClose.length - 1, inputClose, 30, begIdx, length, value));
    }
}
