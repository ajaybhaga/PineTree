/*
 * CCI Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.tictactec.ta.lib.Core;

public class CCI extends StockIndicator {

    public CCI(String name, int size) {
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

        // Calculate CCI
        checkRange(core.cci(0, inputClose.length - 1, inputHigh, inputLow, inputClose, 50, begIdx, length, value));
    }
}
