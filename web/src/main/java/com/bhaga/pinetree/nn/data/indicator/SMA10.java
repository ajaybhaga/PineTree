/*
 * Simple 10-day moving average Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

import com.tictactec.ta.lib.Core;

public class SMA10 extends StockIndicator {

    public SMA10(String name, int size) {
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

        // Calculate SMA 10
        checkRange(core.sma(0, inputClose.length - 1, inputClose, 10, begIdx, length, value));
    }
}
