/*
 * MFI Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

import com.tictactec.ta.lib.Core;

public class MFI extends StockIndicator {

    public MFI(String name, int size) {
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

        // Calculate MFI
        checkRange(core.mfi(0, inputClose.length - 1, inputHigh, inputLow, inputClose, inputVolume, 18, begIdx, length, value));
    }
}
