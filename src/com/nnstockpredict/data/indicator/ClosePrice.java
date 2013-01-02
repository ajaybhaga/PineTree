/*
 * Close Price Indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import java.util.Arrays;

public class ClosePrice extends StockIndicator {

    public ClosePrice(String name, int size) {
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
        value = Arrays.copyOf(inputClose, inputClose.length);
        
        // Manually set the begin and length values
        begIdx.value = 0;
        length.value = value.length;
    }
}
