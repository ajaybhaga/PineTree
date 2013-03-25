/*
 * Average True Range Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

import com.tictactec.ta.lib.Core;

public class ATR extends StockIndicator {

    public ATR(String name, int size) {
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

        // Calculate Average True Range        
        checkRange(core.atr(0, inputClose.length - 1, inputHigh, inputLow, inputClose, 14, begIdx, length, value));
    }
}