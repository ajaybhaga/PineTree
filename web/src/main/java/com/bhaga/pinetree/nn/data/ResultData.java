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
    private double mae[] = new double[NUM_DATA_SETS];
    private double mse[] = new double[NUM_DATA_SETS];
    private double dirMSE[] = new double[NUM_DATA_SETS];
    private double smape[] = new double[NUM_DATA_SETS];
    private double predictivePerc[] = new double[NUM_DATA_SETS];
    private int timeInterval = -1;
    private HashMap<Integer,ChartData> chartDataHashMap = new HashMap<Integer,ChartData>();
    private int numInputs = 0;

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public double[] getDirPredictAcc() {
        return dirPredictAcc;
    }
    
    public double[] getMAE() {
        return mae;
    }

    public double[] getMSE() {
        return mse;
    }

    public double[] getDirMSE() {
        return dirMSE;
    }
    
    public double[] getSMAPE() {
        return smape;
    }
    
    public double[] getPredictivePerc() {
        return predictivePerc;
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
    
    public void setNumInputs(int numInputs) {
        this.numInputs = numInputs;
    }
    
    public int getNumInputs() {
        return numInputs;
    }
}
