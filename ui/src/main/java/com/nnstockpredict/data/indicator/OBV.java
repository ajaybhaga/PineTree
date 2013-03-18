/*
 * On Balance Volume Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.tictactec.ta.lib.Core;

public class OBV extends StockIndicator {

    public OBV(String name, int size) {
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

        // Calculate OBV        
        checkRange(core.obv(0, inputClose.length - 1, inputClose, inputVolume, begIdx, length, value));
    }
}
