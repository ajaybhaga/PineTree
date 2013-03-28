package com.bhaga.pinetree.nn.data;

import java.util.HashMap;
import java.util.Set;

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
    private int timeInterval = -1;
    private HashMap<Integer,ChartData> chartDataHashMap = new HashMap<Integer,ChartData>();

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
    
    public int getTimeInterval() {
        return timeInterval;
    }
    
    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void setChartData(ChartData chartData, int forwardStep) {
        chartDataHashMap.put(forwardStep, chartData);
    }

    public Set<Integer> getChartDataKeySet() {
        return chartDataHashMap.keySet();
    }
    
    public ChartData getChartData(int forwardStep) {
        return chartDataHashMap.get(forwardStep);
    }       
}
