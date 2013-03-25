package com.bhaga.pinetree.ui.data;

import com.bhaga.pinetree.nn.data.ChartData;

/**
 * Store result data of analysis.
 *
 * @author ajaybhaga
 */
public class ResultData {

    public static final int NUM_DATA_SETS = 3;
    private String tickerSymbol;
    private double dirPredictAcc[] = new double[NUM_DATA_SETS];
    private double mse[] = new double[NUM_DATA_SETS];
    private double dirMse[] = new double[NUM_DATA_SETS];
    private ChartData chartData;

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public double[] getDirPredictAcc() {
        return dirPredictAcc;
    }

    public double[] getMse() {
        return mse;
    }

    public double[] getDirMse() {
        return dirMse;
    }

    public void setChartData(ChartData chartData) {
        this.chartData = chartData;
    }

    public ChartData getChartData() {
        return chartData;
    }
}
