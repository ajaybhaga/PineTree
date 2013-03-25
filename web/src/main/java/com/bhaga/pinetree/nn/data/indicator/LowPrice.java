/*
 * Low Price Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.data.indicator;

import java.util.Arrays;

public class LowPrice extends StockIndicator {

    public LowPrice(String name, int size) {
        super(name, size);
    }

    @Override
    public void calculate(
            double[] inputHigh,
            double[] inputLow,
            double[] inputVolume,
            double[] inputOpen,
            double[] inputClose) {

        // Copy close price
        value = Arrays.copyOf(inputLow, inputLow.length);
        
        // Manually set the begin and length values
        begIdx.value = 0;
        length.value = value.length;
    }
}
