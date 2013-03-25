/*
 * A/D (Accumulation/Distribution) Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

import com.tictactec.ta.lib.Core;

public class AD extends StockIndicator {

    public AD(String name, int size) {
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

        // Calculate AD
        checkRange(core.ad(0, inputClose.length - 1, inputHigh, inputLow, inputClose, inputVolume, begIdx, length, value));
    }
}
