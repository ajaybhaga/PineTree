/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nnstockpredict.Utility;

/**
 *
 * @author ajaybhaga
 */
public class Response {

    double value;
    int index;
    double[] valueArr;
    int[] indexArr;

    public Response(double value, int index) {
        this.value = value;
        this.index = index;
    }

    public Response(double[] valueArr, int[] indexArr) {
        this.valueArr = valueArr;
        this.indexArr = indexArr;
    }

    public double getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    public double[] getValueArray() {
        return valueArr;
    }

    public int[] getIndexArray() {
        return indexArr;
    }
}
