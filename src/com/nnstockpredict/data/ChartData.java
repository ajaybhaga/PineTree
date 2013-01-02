package com.nnstockpredict.data;

import com.nnstockpredict.data.indicator.MACD;
import com.nnstockpredict.data.indicator.MACDPeakScore;
import com.nnstockpredict.data.indicator.MACDZeroScore;
import java.util.Date;

/**
 *
 * @author ajaybhaga
 */
public class ChartData {

    private String name;
    private Date[] inputDate;
    private double[] inputOpen;
    private double[] inputHigh;
    private double[] inputLow;
    private double[] inputClose;
    private int maxBegIdx;
    private int maxLength;
    private MACD macd;
    private MACDPeakScore macdPeakScore;
    private MACDZeroScore macdZeroScore;

    public ChartData(String name, Date[] inputDate, double[] inputOpen, double[] inputHigh, double[] inputLow, double[] inputClose, int maxBegIdx, int maxLength, MACD macd, MACDPeakScore macdPeakScore, MACDZeroScore macdZeroScore) {
        this.name = name;
        this.inputDate = inputDate;
        this.inputOpen = inputOpen;
        this.inputHigh = inputHigh;
        this.inputLow = inputLow;
        this.inputClose = inputClose;
        this.maxBegIdx = maxBegIdx;
        this.maxLength = maxLength;
        this.macd = macd;
        this.macdPeakScore = macdPeakScore;
        this.macdZeroScore = macdZeroScore;
    }
    
    public String getName() {
        return name;
    }

    public Date[] getInputDate() {
        return inputDate;
    }

    public double[] getInputOpen() {
        return inputOpen;
    }

    public double[] getInputHigh() {
        return inputHigh;
    }

    public double[] getInputLow() {
        return inputLow;
    }

    public double[] getInputClose() {
        return inputClose;
    }

    public int getMaxBegIdx() {
        return maxBegIdx;
    }
    
    public int getMaxLength() {
        return maxLength;
    }

    public MACD getMACD() {
        return macd;
    }

    public MACDPeakScore getMACDPeakScore() {
        return macdPeakScore;
    }

    public MACDZeroScore getMACDZeroScore() {
        return macdZeroScore;
    }
}
