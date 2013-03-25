/*
 * ADX Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

import com.tictactec.ta.lib.Core;

public class ADX extends StockIndicator {

    public ADX(String name, int size) {
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

        // Calculate ADX
        checkRange(core.adx(0, inputClose.length - 1, inputHigh, inputLow, inputClose, 14, begIdx, length, value));
    }
}
