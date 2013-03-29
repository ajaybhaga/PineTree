package com.bhaga.pinetree.nn.data;

/**
 *
 * @author ajaybhaga
 */
public class NNConfigData {

    private boolean williamsR = false;
    private boolean cci = false;
    private boolean mfi = false;
    private boolean macd = false;
    private boolean williamsRScore = false;
    private boolean macdPeak = false;
    private boolean macdPeakScore = false;
    private boolean macdZeroScore = false;
    private boolean mfiCciZeroScore = false;
    private boolean candlesticks = false;

    public void setWilliamsR(boolean williamsR) {
        this.williamsR = williamsR;
    }

    public boolean getWilliamsR() {
        return williamsR;
    }

    public void setCCI(boolean cci) {
        this.cci = cci;
    }

    public boolean getCCI() {
        return cci;
    }

    public void setMFI(boolean mfi) {
        this.mfi = mfi;
    }

    public boolean getMFI() {
        return mfi;
    }

    public void setMACD(boolean macd) {
        this.macd = macd;
    }

    public boolean getMACD() {
        return macd;
    }

    public void setWilliamsRScore(boolean williamsRScore) {
        this.williamsRScore = williamsRScore;
    }

    public boolean getWilliamsRScore() {
        return williamsRScore;
    }

    public void setMacdPeak(boolean macdPeak) {
        this.macdPeak = macdPeak;
    }

    public boolean getMacdPeak() {
        return macdPeak;
    }

    public void setMacdPeakScore(boolean macdPeakScore) {
        this.macdPeakScore = macdPeakScore;
    }

    public boolean getMacdPeakScore() {
        return macdPeakScore;
    }

    public void setMacdZeroScore(boolean macdZeroScore) {
        this.macdZeroScore = macdZeroScore;
    }

    public boolean getMacdZeroScore() {
        return macdZeroScore;
    }

    public void setMfiCciZeroScore(boolean mfiCciZeroScore) {
        this.mfiCciZeroScore = mfiCciZeroScore;
    }

    public boolean getMfiCciZeroScore() {
        return mfiCciZeroScore;
    }
    
    public void setCandlesticks(boolean candlesticks) {
        this.candlesticks = candlesticks;
    }
    
    public boolean getCandlesticks() {
        return candlesticks;
    }
}
