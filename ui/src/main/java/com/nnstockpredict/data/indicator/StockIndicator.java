/*
 * A stock indicator
 */
/*
 * Author:  Ajay Bhaga
 */
package com.nnstockpredict.data.indicator;

import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.encog.util.arrayutil.NormalizeArray;

public abstract class StockIndicator {

    // Shared hash map
    private final int INF_MIN = -999999;
    private final int INF_MAX = 999999;
    protected String name;
    protected double[] value = null;
    protected double[] normValue = null;
    protected double min = INF_MIN;
    protected double max = INF_MAX;
    protected boolean rangeSet = false;
    protected boolean useAsInput = false;
    protected MInteger begIdx = new MInteger();
    protected MInteger length = new MInteger();

    public StockIndicator(String name, int size) {
        this.name = name;
        value = new double[size];
    }

    public String getName() {
        return name;
    }

    public void setUseAsInput(boolean useAsInput) {
        this.useAsInput = useAsInput;
    }

    public boolean useAsInput() {
        return useAsInput;
    }

    public double getMinValue() {
        double minValue = 99999;
        for (int i = 0; i < length.value; i++) {
            minValue = Math.min(minValue, value[i]);
        }
        return minValue;
    }

    public double getMaxValue() {
        double maxValue = 0;
        for (int i = 0; i < length.value; i++) {
            maxValue = Math.max(maxValue, value[i]);
        }
        return maxValue;
    }

    public double[] getValues() {
        return value;
    }

    public double[] getNormValues() {
        return normValue;
    }

    public int getBegIdx() {
        return begIdx.value;
    }

    public int getLength() {
        return length.value;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        rangeSet = true;
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        rangeSet = true;
        this.max = max;
    }

    public boolean isInRange(double value) {
        if (rangeSet) {
            if ((value >= min) && (value <= max)) {
                return true;
            } else {
                return false;
            }
        } else {
            // No range set, so all is in range
            return true;
        }
    }

    /**
     * Get most recent value.
     *
     * @return the most recent value
     */
    public double getMostRecentValue() {
        return value[length.value - 1];
    }

    /**
     * Get most recent normalized value.
     *
     * @return the most recent normalized value
     */
    public double getMostRecentNormValue() {
        return normValue[length.value - 1];
    }

    @Override
    public String toString() {
        String outStr = "";
        outStr = "[";
        for (int i = 0; i < length.value; i++) {
            outStr = outStr + value[i];
            if (i != length.value - 1) {
                outStr = outStr + ",";
            }
        }
        outStr = outStr + "]";
        return outStr;
    }

    public void checkRange(RetCode retCode) {
        if (retCode == RetCode.Success) {
            for (int i = 0; i < value.length; i++) {
                if (!isInRange(value[i])) {
                    System.out.println(name + ": Reject out-of-range value: " + value[i] + " (min: " + this.getMin() + ", max: " + this.getMax() + ")");
                }
            }
        } else {
            System.err.println("Error calculating " + name + ".");
            System.exit(1);
        }
    }

    public void normalize() {

        // Normalize set
        NormalizeArray normArr = new NormalizeArray();
        normArr.setNormalizedHigh(1);
        normArr.setNormalizedLow(-1);
        //normValue = normArr.process(commonValidValue);
        normValue = normArr.process(value);
    }

    public String normValueToString() {
        String outStr = "";
        outStr = "[";
        for (int i = 0; i < normValue.length; i++) {
            outStr = outStr + normValue[i];
            if (i != normValue.length - 1) {
                outStr = outStr + ",";
            }
        }
        outStr = outStr + "]";
        return outStr;
    }

    public abstract void calculate(
            double[] inputHigh,
            double[] inputLow,
            double[] inputVolume,
            double[] inputOpen,
            double[] inputClose);
}
