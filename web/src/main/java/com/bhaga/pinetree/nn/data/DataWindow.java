/*
 */
package com.bhaga.pinetree.nn.data;

import com.bhaga.pinetree.nn.Utility.ArrayUtil;
import java.util.Arrays;
import org.encog.ml.data.basic.BasicMLData;

/**
 *
 * @author ajaybhaga
 */
public class DataWindow {

    // Data window
    private BasicMLData[] windowArray = null;
    // Direction of movement after the window
    private double direction = 0.0;

    public DataWindow(BasicMLData[] windowArray) {
        this.windowArray = windowArray;
    }

    public int getWindowSize() {
        return windowArray.length;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public BasicMLData getData() {
        double[] result = Arrays.copyOf(windowArray[0].getData(), windowArray[0].getData().length);
        for (int i = 1; i < windowArray.length; i++) {
            result = ArrayUtil.convertArray(ArrayUtil.concat(ArrayUtil.convertArray(result), ArrayUtil.convertArray(windowArray[i].getData())));
        }
        return new BasicMLData(result);
    }

    public double[] getDirectionArray() {
        double[] directionArray = new double[1];
        directionArray[0] = direction;
        return directionArray;
    }

    @Override
    public String toString() {
        String result = "";
        BasicMLData data = getData();

        result = "[";
        for (int i = 0; i < data.getData().length; i++) {
            double value = data.getData(i);
            result = result + value;
            if (i != data.getData().length - 1) {
                result = result + ",";
            }
        }
        result = result + "]";
        return result;
    }
}
