/*
 * William's R% Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

import com.tictactec.ta.lib.Core;

public class WilliamsR extends StockIndicator {

    public WilliamsR(String name, int size) {
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
        setMin(-120);
        setMax(20);

        // Calculate William's %R
        checkRange(core.willR(0, inputClose.length - 1, inputHigh, inputLow, inputClose, 30, begIdx, length, value));
    }
}
