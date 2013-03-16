package com.nnstockpredict.data;

import com.nnstockpredict.data.indicator.CCI;
import com.nnstockpredict.data.indicator.MACD;
import com.nnstockpredict.data.indicator.MACDPeak;
import com.nnstockpredict.data.indicator.MACDPeakScore;
import com.nnstockpredict.data.indicator.MACDZeroScore;
import com.nnstockpredict.data.indicator.MFI;
import com.nnstockpredict.data.indicator.MFICCIZeroScore;
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
    private double[] forecast;
    private double[] forecastError;
    private double[] forecastDirectionError;
    private int maxBegIdx;
    private int maxLength;
    private MACD macd;    
    private MACDPeak macdPeak;
    private MACDPeakScore macdPeakScore;
    private MACDZeroScore macdZeroScore;
    private MFI mfi;
    private CCI cci;
    private MFICCIZeroScore mfiCciZeroScore;

    public ChartData(String name, Date[] inputDate, double[] inputOpen, double[] inputHigh, double[] inputLow, double[] inputClose, double[] forecast, double[] forecastError, double[] forecastDirectionError, int maxBegIdx, int maxLength, MACD macd, MACDPeak macdPeak, MACDPeakScore macdPeakScore, MACDZeroScore macdZeroScore, MFI mfi, CCI cci, MFICCIZeroScore mfiCciZeroScore) {
        this.name = name;
        this.inputDate = inputDate;
        this.inputOpen = inputOpen;
        this.inputHigh = inputHigh;
        this.inputLow = inputLow;
        this.inputClose = inputClose;
        this.forecast = forecast;
        this.forecastError = forecastError;
        this.forecastDirectionError = forecastDirectionError;
        this.maxBegIdx = maxBegIdx;
        this.maxLength = maxLength;
        this.macd = macd;
        this.macdPeak = macdPeak;
        this.macdPeakScore = macdPeakScore;
        this.macdZeroScore = macdZeroScore;
        this.mfi = mfi;
        this.cci = cci;
        this.mfiCciZeroScore = mfiCciZeroScore;
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

    public double[] getForecast() {
        return forecast;
    }

    public double[] getForecastError() {
        return forecastError;
    }

    public double[] getForecastDirectionError() {
        return forecastDirectionError;
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

    public MACDPeak getMACDPeak() {
        return macdPeak;
    }

    public MACDPeakScore getMACDPeakScore() {
        return macdPeakScore;
    }

    public MACDZeroScore getMACDZeroScore() {
        return macdZeroScore;
    }

    public MFI getMFI() {
        return mfi;
    }

    public CCI getCCI() {
        return cci;
    }

    public MFICCIZeroScore getMFICCIZeroScore() {
        return mfiCciZeroScore;
    }
}
