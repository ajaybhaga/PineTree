package com.nnstockpredict.data;

import com.nnstockpredict.Config;
import com.nnstockpredict.Utility.CandlestickUtility;
import com.nnstockpredict.Utility.FileUtils;
import com.nnstockpredict.Utility.loader.EODFinanceLoader;
import com.nnstockpredict.data.candlestick.CandlestickPattern;
import com.nnstockpredict.data.indicator.*;
import com.nnstockpredict.fuzzy.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.TickerSymbol;
import org.encog.ml.data.market.loader.LoadedMarketData;
import org.encog.ml.data.market.loader.MarketLoader;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizeArray;
import org.encog.util.simple.EncogUtility;

public class PrepareData {

    public final static double COLOUR_BLACK = 0.5;
    public final static double COLOUR_CROSS = 1.0;
    public final static double COLOUR_WHITE = 1.5;
    /**
     * The serial id.
     */
    private static final long serialVersionUID = 170791819906003867L;
    /**
     * The loader to use to obtain the data.
     */
    private final EODFinanceLoader loader;
    private final int stepSize;
    private File dataDir;
    private MLDataSet trainingSet = null;
    private MLDataSet evaluationSet = null;
    private LinguisticVariable variation = null;

    public PrepareData(EODFinanceLoader loader, File dataDir, int stepSize) {
        this.loader = loader;
        this.dataDir = dataDir;
        this.stepSize = stepSize;
    }

    public double getDirection(double value) {
        if (value > 0.0D) {
            return 1.0D;
        } else if (value == 0.0D) {
            return 0.0D;
        } else {
            return -1.0D;
        }
    }

    public MLDataSet getTrainingSet() {
        return trainingSet;
    }

    public MLDataSet getEvaluationSet() {
        return evaluationSet;
    }

    public void trainNetwork(BasicNetwork network, MLDataSet training, boolean crossValidation) {

        int epoch = 1;
        double lastError = 0.0;

        String crossValidationStr = (crossValidation) ? "on" : "off";

        System.out.println("Cross Validation is " + crossValidationStr + ".");


        double initialError = -9999.0D;
        double error = 0.0D;

        if (!crossValidation) {

            final Train train = new ResilientPropagation(network, training);

            do {
                train.iteration();
                /*
                 if (initialError == -9999.0D) {
                 initialError = train.getError();

                 if (initialError < 0.03D) {
                 error = initialError - 0.02D;
                 } else {
                 error = 0.03D;
                 }
                 }*/
                error = 0.01D;

                System.out.println("TRAINING - Epoch #" + epoch + " Error:" + train.getError());
                epoch++;

                if (Math.abs(lastError - train.getError()) < 0.000000000001) {
                    System.out.println("Error difference too low (" + Math.abs(lastError - train.getError()) + ") ending training at Epoch #" + epoch + " Error:" + train.getError());
                    break;
                }

                if (epoch > 50000) {
                    break;
                }

                // Store the error
                lastError = train.getError();
            } while (train.getError() > error);
            //} while (train.getIteration() < 10000);

        } else {

            final FoldedDataSet folded = new FoldedDataSet(training);
            final MLTrain train = new ResilientPropagation(network, folded);
            final CrossValidationKFold trainFolded = new CrossValidationKFold(train, 4);

            do {
                //train.iteration();
                trainFolded.iteration();
                /*
                 if (initialError == -9999.0D) {
                 initialError = train.getError();

                 if (initialError < 0.05D) {
                 error = initialError - 0.02D;
                 } else {
                 error = 0.05D;
                 }
                 }*/
                error = 0.01D;

                //  System.out.println("TRAINING - Epoch #" + epoch + " Error:" + train.getError());
                System.out.println("TRAINING - Epoch #" + epoch + " Error:" + trainFolded.getError());
                epoch++;

                if (Math.abs(lastError - trainFolded.getError()) < 0.000000000001) {
                    System.out.println("Error difference too low (" + Math.abs(lastError - trainFolded.getError()) + ") ending training at Epoch #" + epoch + " Error:" + trainFolded.getError());
                    break;
                }

                // Store the error
                lastError = trainFolded.getError();
            } while (trainFolded.getError() > error);
        }
    }

    /**
     * Load data from the loader.
     *
     * @param begin The beginning date.
     * @param end The ending date.
     */
    public void load(String symbol, final Date begin, final Date end, final int forwardStep) {
        prepare(new TickerSymbol(symbol, "AMEX"), begin, end, forwardStep);
    }

    /**
     * Prepare data for one ticker symbol.
     *
     * @param ticker The ticker symbol to load.
     * @param from Load data from this date.
     * @param to Load data to this date.
     * @param forwardStep Number of time steps forward into time to predict
     */
    private void prepare(TickerSymbol ticker, final Date from, final Date to, int forwardStep) {
        final Collection<LoadedMarketData> data = loader.load(ticker,
                null, from, to, stepSize);

        System.out.println("Loaded data set: " + data.size());

        double[] inputHigh = new double[data.size()];
        double[] inputLow = new double[data.size()];
        double[] inputVolume = new double[data.size()];
        double[] inputOpen = new double[data.size()];
        double[] inputClose = new double[data.size()];
        double[] direction = new double[data.size()];

        Date[] inputDate = new Date[data.size() + forwardStep];

        int i = 0;
        for (final LoadedMarketData item : data) {
            inputHigh[i] = item.getData(MarketDataType.HIGH);
            inputLow[i] = item.getData(MarketDataType.LOW);
            inputVolume[i] = item.getData(MarketDataType.VOLUME);
            inputOpen[i] = item.getData(MarketDataType.OPEN);
            inputClose[i] = item.getData(MarketDataType.CLOSE);
            inputDate[i] = item.getWhen();

            // Verify that we're using the correct close
            if (inputClose[i] > inputHigh[i]) {
                System.out.println("IMPOSSIBLE! inputClose[i] > inputHigh[i]: " + inputClose[i] + ", " + inputHigh[i]);
                System.exit(1);
            }

            if (i > 0) {
                if (inputClose[i - 1] <= inputClose[i]) {
                    direction[i - 1] = 1;
                } else {
                    direction[i - 1] = -1;
                }
            }
            i++;
        }

        // Calculate all the forward step dates and store them
        for (int j = 1; j <= forwardStep; j++) {
            // Add the next time step into the date array
            Calendar c = Calendar.getInstance();
            // We want this calendar at the last non-predicted data point
            c.setTime(inputDate[data.size() - 1]);
            // Move forward by the time step
            c.add(stepSize, j);
            // Store the date
            inputDate[(data.size() - 1) + j] = c.getTime();
        }

        List<StockIndicator> stockIndicatorList = new ArrayList<StockIndicator>(10);
        /*
         OpenPrice openPrice = new OpenPrice("Open Price", inputOpen.length);
         //openPrice.setUseAsInput(true);
         openPrice.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(openPrice);

         HighPrice highPrice = new HighPrice("High Price", inputHigh.length);
         //highPrice.setUseAsInput(true);
         highPrice.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(highPrice);

         LowPrice lowPrice = new LowPrice("Low Price", inputLow.length);
         //lowPrice.setUseAsInput(true);
         lowPrice.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(lowPrice);

         ClosePrice closePrice = new ClosePrice("Close Price", inputClose.length);
         closePrice.setUseAsInput(true);
         closePrice.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(closePrice);
         */
        /*    RSI rsi = new RSI("RSI", inputClose.length);
         rsi.setUseAsInput(true);
         rsi.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(rsi);

         Stoch stoch = new Stoch("Stoch %D", inputClose.length);
         stoch.setUseAsInput(true);
         stoch.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(stoch);*/
        /*
         StochF stochF = new StochF("Stoch %K", inputClose.length);
         stochF.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(stochF);
         */
        WilliamsR williamsR = new WilliamsR("Williams %R", inputClose.length);
        //williamsR.setUseAsInput(true);
        williamsR.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(williamsR);
        /*
         EMA5 ema5 = new EMA5("EMA 5", inputClose.length);
         ema5.setUseAsInput(true);
         ema5.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(ema5);

         EMA10 ema10 = new EMA10("EMA 10", inputClose.length);
         ema10.setUseAsInput(true);
         ema10.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(ema10);*/
        /*
         EMA30 ema30 = new EMA30("EMA 30", inputClose.length);
         ema30.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(ema30);
         */

        /*
         SMA10 sma10 = new SMA10("SMA 10", inputClose.length);
         sma10.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(sma10);
         WMA10 wma10 = new WMA10("WMA 10", inputClose.length);
         wma10.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(wma10);*/



        CCI cci = new CCI("CCI", inputClose.length);
        //RESTORE
        //cci.setUseAsInput(true);
        cci.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(cci);

        /*
         * AD ad = new AD("AD", inputClose.length); ad.calculate(inputHigh,
         * inputLow, inputVolume, inputOpen, inputClose);
         * stockIndicatorList.add(ad);
         */
        /*
         MFI mfi = new MFI("MFI", inputClose.length);
         mfi.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(mfi);

         ATR atr = new ATR("ATR", inputClose.length);
         atr.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(atr);*/
        /*
         * OBV obv = new OBV("OBV", inputClose.length); obv.calculate(inputHigh,
         * inputLow, inputVolume, inputOpen, inputClose);
         * stockIndicatorList.add(obv);
         */


        MFI mfi = new MFI("MFI", inputClose.length);
        //RESTORE
        //mfi.setUseAsInput(true);
        mfi.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(mfi);

        /*
         ADX adx = new ADX("ADX", inputClose.length);
         adx.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(adx);

         DIPlus diPlus = new DIPlus("DI Plus", inputClose.length);
         diPlus.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(diPlus);


         DIMinus diMinus = new DIMinus("DI Minus", inputClose.length);
         diMinus.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(diMinus);
         */
        MACD macd = new MACD("MACD", inputClose.length);
        macd.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        //   macd.setUseAsInput(true);
        stockIndicatorList.add(macd);




        int maxBegIdx = 0;

        // Determine the common start index of all indicators
        for (StockIndicator stockIndicator : stockIndicatorList) {
            maxBegIdx = Math.max(maxBegIdx, stockIndicator.getBegIdx());
        }

        int maxLength = inputClose.length - maxBegIdx;

        double[] forecast = new double[maxLength + forwardStep];
        double[] forecastError = new double[maxLength + forwardStep];
        double[] forecastDirectionError = new double[maxLength + forwardStep];

        double[] inputC0BodyColour = new double[maxLength];
        double[] inputC0Body = new double[maxLength];
        double[] inputC0UpperShadow = new double[maxLength];
        double[] inputC0LowerShadow = new double[maxLength];
        double[] inputC0OpenStyle = new double[maxLength];
        double[] inputC0CloseStyle = new double[maxLength];

        double[] outputVariation = new double[maxLength];

        // Put all scoring indicators last
        WilliamsRScore williamsRScore = new WilliamsRScore("Williams R Score", maxLength);
        williamsRScore.calculate(williamsR.getValues(), williamsR.getBegIdx(), maxBegIdx, maxLength);
        williamsRScore.setUseAsInput(true);
        stockIndicatorList.add(williamsRScore);
        /*
         ADXTrendScore adxTrendScore = new ADXTrendScore("ADX Trend Score", maxLength);
         adxTrendScore.calculate(adx.getValues(), adx.getBegIdx(), diPlus.getValues(), diPlus.getBegIdx(), diMinus.getValues(), diMinus.getBegIdx(), maxBegIdx, maxLength);
         adxTrendScore.setUseAsInput(true);
         stockIndicatorList.add(adxTrendScore);
         */

        MACDPeak macdPeak = new MACDPeak("MACD Peak", maxLength);
        macdPeak.calculate(inputClose, macd.getValues(), macd.getBegIdx(), maxBegIdx, maxLength);
        macdPeak.setUseAsInput(true);
        stockIndicatorList.add(macdPeak);

        MACDPeakScore macdPeakScore = new MACDPeakScore("MACD Peak Score", maxLength);
        macdPeakScore.calculate(inputClose, macd.getValues(), macd.getBegIdx(), maxBegIdx, maxLength);
        macdPeakScore.setUseAsInput(true);
        stockIndicatorList.add(macdPeakScore);

        MACDZeroScore macdZeroScore = new MACDZeroScore("MACD Zero Score", maxLength);
        macdZeroScore.calculate(macd.getValues(), macd.getBegIdx(), maxBegIdx, maxLength);
        macdZeroScore.setUseAsInput(true);
        stockIndicatorList.add(macdZeroScore);

        MFICCIZeroScore mfiCciZeroScore = new MFICCIZeroScore("MFI CCI Zero Score", maxLength);
        mfiCciZeroScore.calculate(mfi.getValues(), mfi.getBegIdx(), cci.getValues(), cci.getBegIdx(), maxBegIdx, maxLength);
        mfiCciZeroScore.setUseAsInput(true);
        stockIndicatorList.add(mfiCciZeroScore);


        /* ERSTrendScore ersTrendScore = new ERSTrendScore("ERS Trend Score", maxLength);
         ersTrendScore.calculate(ema5.getValues(), ema5.getBegIdx(), ema10.getValues(), ema10.getBegIdx(), rsi.getValues(), rsi.getBegIdx(), stochF.getValues(), stochF.getBegIdx(), stoch.getValues(), stoch.getBegIdx(), maxBegIdx, maxLength);
         ersTrendScore.setUseAsInput(true);
         stockIndicatorList.add(ersTrendScore);
         */

        /*
         System.out.println("EMA5 = " + ema5.getMostRecentValue() + ", idx = " + (ema5.getLength() - 1));
         System.out.println("EMA10 = " + ema10.getMostRecentValue() + ", idx = " + (ema10.getLength() - 1));
         System.out.println("EMA30 = " + ema30.getMostRecentValue() + ", idx = " + (ema30.getLength() - 1));
         System.out.println("RSI = " + rsi.getMostRecentValue() + ", idx = " + (rsi.getLength() - 1));
         System.out.println("Stoch = " + stoch.getMostRecentValue() + ", idx = " + (stoch.getLength() - 1));
         System.out.println("Stoch F = " + stochF.getMostRecentValue() + ", idx = " + (stochF.getLength() - 1));
         System.out.println("ERS = " + ersTrendScore.getMostRecentValue() + ", idx = " + (ersTrendScore.getLength() - 1));

         System.out.println("Williams R = " + williamsR.getMostRecentValue());
         System.out.println("DI+ = " + diPlus.getMostRecentValue());
         System.out.println("DI- = " + diMinus.getMostRecentValue());
         */
        System.out.println("MACDBegIdx = " + macd.getBegIdx());
        System.out.println("MACD Length = " + macd.getLength());

        System.out.println("Max begin index: " + maxBegIdx);

        System.out.println("maxBegIdx { inputDate: " + inputDate[maxBegIdx] + ", price: " + inputClose[maxBegIdx] + " }");
        System.out.println("maxLength { inputDate: " + inputDate[maxBegIdx + maxLength - 1] + ", price: " + inputClose[maxBegIdx + maxLength - 1] + " }");

        // Load candlestick patterns
        CandlestickUtility.loadPatterns();

        double low = 0;
        double equal_low = 0;
        double equal = 0;
        double equal_high = 0;
        double high = 0;

        List<Integer> patternIdList = new LinkedList<Integer>();
        int patternCount = 0;

        // Calculate variation of percentage max and min
        double maxD = -9999.0D;
        double minD = 9999.0D;
        int varLookBack = 120; // Look back n days
        for (int j = (maxBegIdx + maxLength) - varLookBack; j < maxBegIdx + maxLength; j++) {
            if (j + forwardStep < maxBegIdx + maxLength) {

                // Calculate variation of percentage
                double varPct = ((inputClose[j + forwardStep] - inputClose[j]) / inputClose[j]) * 100.0D;

                //System.out.println("varPct: " + varPct + ", inputClose[j]=" + inputClose[j] + ", inputClose[j+1]=" + inputClose[j+1]);
                minD = Math.min(varPct, minD);
                maxD = Math.max(varPct, maxD);
            }
        }

        // Calculate U max and min
        double minU = Math.floor(minD);
        double maxU = Math.ceil(maxD);

        double limitU = Math.max(Math.abs(minU), Math.abs(maxU));
        maxU = Math.abs(limitU);
        minU = -Math.abs(limitU);

        double stepU = (maxU - minU) / 4.0D;
        minU = minU - (stepU / 2.0D);

        variation = new LinguisticVariable("variation");

        System.out.println("minU: " + minU);
        System.out.println("maxU: " + maxU);
        System.out.println("stepU: " + stepU);

        int currU = 0;
        variation.add("extremeDecrease", minU + (stepU * currU), minU + ((stepU / 2.0D) * (currU + 1)), minU + ((stepU / 2.0D) * (currU + 1)), minU + (stepU * (currU + 1)));
        System.out.println("extremeDecrease=[" + (minU + (stepU * currU)) + "," + (minU + ((stepU / 2.0D) * (currU + 1))) + "," + (minU + ((stepU / 2.0D) * (currU + 1))) + "," + (minU + (stepU * (currU + 1))) + "].");
        currU++;

        while (currU < 9) {
            String membershipName = "";

            switch (currU) {
                case 1:
                    membershipName = "largeDecrease";
                    break;
                case 2:
                    membershipName = "normalDecrease";
                    break;
                case 3:
                    membershipName = "smallDecrease";
                    break;
                case 4:
                    membershipName = "noChange";
                    break;
                case 5:
                    membershipName = "smallIncrease";
                    break;
                case 6:
                    membershipName = "normalIncrease";
                    break;
                case 7:
                    membershipName = "largeIncrease";
                    break;
                case 8:
                    membershipName = "extremeIncrease";
                    break;
            }

            variation.add(membershipName, minU + ((stepU / 2.0D) * currU), minU + ((stepU / 2.0D) * (currU + 1)), minU + ((stepU / 2.0D) * (currU + 1)), minU + ((stepU / 2.0D) * (currU + 2)));
            System.out.println(membershipName + "=[" + (minU + ((stepU / 2.0D) * currU)) + "," + (minU + ((stepU / 2.0D) * (currU + 1))) + "," + (minU + ((stepU / 2.0D) * (currU + 1))) + "," + (minU + ((stepU / 2.0D) * (currU + 2))) + "].");
            currU++;
        }

        double thirtyDayMin = 9999.0D;
        double thirtyDayMax = -9999.0D;
        // Determine the 30 day fluctuation price
        for (int j = (maxBegIdx + maxLength) - 30; j < maxBegIdx + maxLength; j++) {
            thirtyDayMin = Math.min(inputClose[j], thirtyDayMin);
            thirtyDayMax = Math.max(inputClose[j], thirtyDayMax);
        }

        double thirtyDayFluctTotal = thirtyDayMax - thirtyDayMin;

        System.out.println("thirtyDayMax: " + thirtyDayMax);
        System.out.println("thirtyDayMin: " + thirtyDayMin);

        double short_start = (0.5 / 100.0) * thirtyDayFluctTotal;
        double short_end = (1.5 / 100.0) * thirtyDayFluctTotal;
        double middle_start = (2.5 / 100.0) * thirtyDayFluctTotal;
        double middle_end = (3.5 / 100.0) * thirtyDayFluctTotal;
        double long_start = (5.0 / 100.0) * thirtyDayFluctTotal;
        double long_end = (25.0 / 100.0) * thirtyDayFluctTotal;

        for (int j = maxBegIdx; j < maxBegIdx + maxLength; j++) {

            FuzzyEngine fuzzyEngine = null;
            FuzzyBlockOfRules fuzzyRules;
            LinguisticVariable trend = null;
            LinguisticVariable c0Body = null;
            LinguisticVariable c0BodyColour = null;
            LinguisticVariable c0OpenStyle = null;
            LinguisticVariable c0CloseStyle = null;
            LinguisticVariable c0UpperShadow = null;
            LinguisticVariable c0LowerShadow = null;
            LinguisticVariable c1Body = null;
            LinguisticVariable c1BodyColour = null;
            LinguisticVariable c1OpenStyle = null;
            LinguisticVariable c1CloseStyle = null;
            LinguisticVariable c1UpperShadow = null;
            LinguisticVariable c1LowerShadow = null;
            LinguisticVariable pattern = null;

            fuzzyEngine = new FuzzyEngine();
            trend = new LinguisticVariable("trend");
            trend.add("down", 0.0D, 0.5D, 0.5D, 1.0D);
            trend.add("consolidating", 0.5D, 1.0D, 1.0D, 1.5D);
            trend.add("up", 1.0D, 1.5D, 1.5D, 2.0D);

            String patternName;
            int patternId;
            pattern = new LinguisticVariable("pattern");

            Iterator it = CandlestickUtility.getMap().keySet().iterator();
            while (it.hasNext()) {
                int id = (Integer) it.next();
                CandlestickPattern candlestickPattern = (CandlestickPattern) CandlestickUtility.getMap().get(id);

                patternName = candlestickPattern.getName();
                patternId = candlestickPattern.getId();
                pattern.add(patternName, patternId - 2.0D, patternId, patternId, patternId + 2.0D);
            }

            c0BodyColour = new LinguisticVariable("c0BodyColour");
            c0BodyColour.add("black", 0.0D, 0.5D, 0.5D, 1.0D);
            c0BodyColour.add("cross", 0.5D, 1.0D, 1.0D, 1.5D);
            c0BodyColour.add("white", 1.0D, 1.5D, 1.5D, 2.0D);

            c1BodyColour = new LinguisticVariable("c1BodyColour");
            c1BodyColour.add("black", 0.0D, 0.5D, 0.5D, 1.0D);
            c1BodyColour.add("cross", 0.5D, 1.0D, 1.0D, 1.5D);
            c1BodyColour.add("white", 1.0D, 1.5D, 1.5D, 2.0D);

            c0Body = new LinguisticVariable("c0Body");
            c0Body.add("equal", 0.0D, (short_start / 2.0D), (short_start / 2.0D), short_start);
            c0Body.add("short", 0.0D, short_start, short_end, middle_start);
            c0Body.add("middle", short_end, middle_start, middle_end, long_start);
            c0Body.add("long", middle_end, long_start, long_end, long_end);

            c1Body = new LinguisticVariable("c1Body");
            c1Body.add("equal", 0.0D, (short_start / 2.0D), (short_start / 2.0D), short_start);
            c1Body.add("short", 0.0D, short_start, short_end, middle_start);
            c1Body.add("middle", short_end, middle_start, middle_end, long_start);
            c1Body.add("long", middle_end, long_start, long_end, long_end);

            //System.out.println("FUZZY I/O { inputDate: " + inputDate[j] + " } -----------------------------------");

            // Body values
            //System.out.println("short_start_value: " + short_start);
            //System.out.println("short_start_value_plus_one: " + short_start + 1);
            //System.out.println("short_end_value: " + short_end);
            //System.out.println("middle_start_value: " + middle_start);
            //System.out.println("middle_start_value_plus_one: " + middle_start + 1);
            //System.out.println("middle_end_value: " + middle_end);
            //System.out.println("long_start_value:" + long_start);
            //System.out.println("long_end_value:" + long_end);




            // Retrieve open/close style for previous day

            // Do min to be inclusive of current candle being lesser than previous
            low = Math.min(inputLow[j - 1], inputLow[j]);
            // Do max to be inclusive of current candle being larger than previous
            high = Math.max(inputHigh[j - 1], inputHigh[j]);
            equal_low = Math.min(inputOpen[j - 1], inputClose[j - 1]);
            equal_high = Math.max(inputOpen[j - 1], inputClose[j - 1]);
            equal = (equal_low + equal_high) / 2.0;

            c0OpenStyle = new LinguisticVariable("c0OpenStyle");
            c0OpenStyle.add("low", low, low, low, equal_low);
            c0OpenStyle.add("equal_low", low, equal_low, equal_low, equal);
            c0OpenStyle.add("equal", equal_low, equal, equal, equal_high);
            c0OpenStyle.add("equal_high", equal, equal_high, equal_high, high);
            c0OpenStyle.add("high", equal_high, high, high, high);

            c0CloseStyle = new LinguisticVariable("c0CloseStyle");
            c0CloseStyle.add("low", low, low, low, equal_low);
            c0CloseStyle.add("equal_low", low, equal_low, equal_low, equal);
            c0CloseStyle.add("equal", equal_low, equal, equal, equal_high);
            c0CloseStyle.add("equal_high", equal, equal_high, equal_high, high);
            c0CloseStyle.add("high", equal_high, high, high, high);

            c0UpperShadow = new LinguisticVariable("c0UpperShadow");
            c0UpperShadow.add("equal", 0.0D, (short_start / 2.0D), (short_start / 2.0D), short_start);
            c0UpperShadow.add("short", 0.0D, short_start, short_end, middle_start);
            c0UpperShadow.add("middle", short_end, middle_start, middle_end, long_start);
            c0UpperShadow.add("long", middle_end, long_start, long_end, long_end);

            c0LowerShadow = new LinguisticVariable("c0LowerShadow");
            c0LowerShadow.add("equal", 0.0D, (short_start / 2.0D), (short_start / 2.0D), short_start);
            c0LowerShadow.add("short", 0.0D, short_start, short_end, middle_start);
            c0LowerShadow.add("middle", short_end, middle_start, middle_end, long_start);
            c0LowerShadow.add("long", middle_end, long_start, long_end, long_end);

            // Do min to be inclusive of current candle being lesser than previous
            low = Math.min(inputLow[j - 2], inputLow[j - 1]);
            // Do max to be inclusive of current candle being larger than previous
            high = Math.max(inputHigh[j - 2], inputHigh[j - 1]);
            equal_low = Math.min(inputOpen[j - 2], inputClose[j - 2]);
            equal_high = Math.max(inputOpen[j - 2], inputClose[j - 2]);
            equal = (equal_low + equal_high) / 2.0;

            c1OpenStyle = new LinguisticVariable("c1OpenStyle");
            c1OpenStyle.add("low", low, low, low, equal_low);
            c1OpenStyle.add("equal_low", low, equal_low, equal_low, equal);
            c1OpenStyle.add("equal", equal_low, equal, equal, equal_high);
            c1OpenStyle.add("equal_high", equal, equal_high, equal_high, high);
            c1OpenStyle.add("high", equal_high, high, high, high);

            c1CloseStyle = new LinguisticVariable("c1CloseStyle");
            c1CloseStyle.add("low", low, low, low, equal_low);
            c1CloseStyle.add("equal_low", low, equal_low, equal_low, equal);
            c1CloseStyle.add("equal", equal_low, equal, equal, equal_high);
            c1CloseStyle.add("equal_high", equal, equal_high, equal_high, high);
            c1CloseStyle.add("high", equal_high, high, high, high);

            c1UpperShadow = new LinguisticVariable("c1UpperShadow");
            c1UpperShadow.add("equal", 0.0D, (short_start / 2.0D), (short_start / 2.0D), short_start);
            c1UpperShadow.add("short", 0.0D, short_start, short_end, middle_start);
            c1UpperShadow.add("middle", short_end, middle_start, middle_end, long_start);
            c1UpperShadow.add("long", middle_end, long_start, long_end, long_end);

            c1LowerShadow = new LinguisticVariable("c1LowerShadow");
            c1LowerShadow.add("equal", 0.0D, (short_start / 2.0D), (short_start / 2.0D), short_start);
            c1LowerShadow.add("short", 0.0D, short_start, short_end, middle_start);
            c1LowerShadow.add("middle", short_end, middle_start, middle_end, long_start);
            c1LowerShadow.add("long", middle_end, long_start, long_end, long_end);

            // Retrieve fuzzy rules
            String rulesString = CandlestickUtility.getAllRules();

            // Initialize fuzzy engine
            fuzzyRules = new FuzzyBlockOfRules(rulesString);
            fuzzyEngine.register(variation);
            fuzzyEngine.register(trend);
            fuzzyEngine.register(c0Body);
            fuzzyEngine.register(c0BodyColour);
            fuzzyEngine.register(c0OpenStyle);
            fuzzyEngine.register(c0CloseStyle);
            fuzzyEngine.register(c0UpperShadow);
            fuzzyEngine.register(c0LowerShadow);
            fuzzyEngine.register(c1Body);
            fuzzyEngine.register(c1BodyColour);
            fuzzyEngine.register(c1OpenStyle);
            fuzzyEngine.register(c1CloseStyle);
            fuzzyEngine.register(c1UpperShadow);
            fuzzyEngine.register(c1LowerShadow);
            fuzzyEngine.register(pattern);

            fuzzyEngine.register(fuzzyRules);
            try {
                fuzzyRules.parseBlock();
            } catch (RulesParsingException ex) {
                Logger.getLogger(PrepareData.class.getName()).log(Level.SEVERE, null, ex);
            }

            //fuzzyEngine.reset();


            if (j + forwardStep < maxBegIdx + maxLength) {
                // Calculate variation of percentage
                double varPct = ((inputClose[j + forwardStep] - inputClose[j]) / inputClose[j]) * 100.0D;
                //System.out.println("variation percentage = " + varPct + "%");
                variation.setInputValue(varPct);
            }

//            trend.setInputValue(((ema5.getValues()[j] > ema10.getValues()[j]) ? 1.5 : (ema5.getValues()[j] == ema10.getValues()[j]) ? 1.0 : 0.5));
            c0Body.setInputValue(Math.max(inputOpen[j], inputClose[j]) - Math.min(inputOpen[j], inputClose[j]));
            c0BodyColour.setInputValue((inputClose[j] > inputOpen[j]) ? COLOUR_WHITE : (inputClose[j] == inputOpen[j]) ? COLOUR_CROSS : COLOUR_BLACK);
            c0OpenStyle.setInputValue(inputOpen[j]);
            c0CloseStyle.setInputValue(inputClose[j]);
            c0UpperShadow.setInputValue(inputHigh[j] - Math.max(inputOpen[j], inputClose[j]));
            c0LowerShadow.setInputValue(Math.min(inputOpen[j], inputClose[j]) - inputLow[j]);

            c1Body.setInputValue(Math.max(inputOpen[j - 1], inputClose[j - 1]) - Math.min(inputOpen[j - 1], inputClose[j - 1]));
            c1BodyColour.setInputValue((inputClose[j - 1] > inputOpen[j - 1]) ? COLOUR_WHITE : (inputClose[j - 1] == inputOpen[j - 1]) ? COLOUR_CROSS : COLOUR_BLACK);
            c1OpenStyle.setInputValue(inputOpen[j - 1]);
            c1CloseStyle.setInputValue(inputClose[j - 1]);
            c1UpperShadow.setInputValue(inputHigh[j - 1] - Math.max(inputOpen[j - 1], inputClose[j - 1]));
            c1LowerShadow.setInputValue(Math.min(inputOpen[j - 1], inputClose[j - 1]) - inputLow[j - 1]);

            // Input values
            //System.out.println("ema5: " + ema5.getValues()[j]);
            //System.out.println("ema10: " + ema10.getValues()[j]);
            //System.out.println("trend: " + ((ema5.getValues()[j] > ema10.getValues()[j]) ? 1 : 0));

            //System.out.println("c0Body: " + c0Body.getInputValue());
            //System.out.println("c0BodyColour: " + c0BodyColour.getInputValue());
            //System.out.println("c0OpenStyle: " + c0OpenStyle.getInputValue());
            //System.out.println("c0CloseStyle: " + c0CloseStyle.getInputValue());
            //System.out.println("c0UpperShadow: " + c0UpperShadow.getInputValue());
            //System.out.println("c0LowerShadow: " + c0LowerShadow.getInputValue());

            //System.out.println("c1Body: " + c1Body.getInputValue());
            //System.out.println("c1BodyColour: " + c1BodyColour.getInputValue());
            //System.out.println("c1OpenStyle: " + c1OpenStyle.getInputValue());
            //System.out.println("c1CloseStyle: " + c1CloseStyle.getInputValue());
            //System.out.println("c1UpperShadow: " + c1UpperShadow.getInputValue());
            //System.out.println("c1LowerShadow: " + c1LowerShadow.getInputValue());


            //if (j == maxBegIdx + maxLength - 1) {
          /*  if (inputDate[j].toString().contains("Wed Oct 05 00:00:00 EDT 2011")) {
             c0Body.chart(true);
             c0BodyColour.chart(true);
             c0OpenStyle.chart(true);
             c0CloseStyle.chart(true);
             c0UpperShadow.chart(true);
             c0LowerShadow.chart(true);

             c1Body.chart(true);
             c1BodyColour.chart(true);
             c1OpenStyle.chart(true);
             c1CloseStyle.chart(true);
             c1UpperShadow.chart(true);
             c1LowerShadow.chart(true);

             variation.chart(true);
             }*/

            Object[] objArray;
            String name;
            double value;

            objArray = variation.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];

            System.out.println("variation = " + name + "[" + value + "]");

            objArray = c0Body.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            System.out.println("c0Body = " + name + " [" + value + "]");

            objArray = c0BodyColour.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            System.out.println("c0BodyColour = " + name + " [" + value + "]");


            objArray = c0OpenStyle.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            System.out.println("c0OpenStyle = " + name + " [" + value + "]");


            objArray = c0CloseStyle.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            System.out.println("c0CloseStyle = " + name + " [" + value + "]");


            objArray = c0UpperShadow.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            System.out.println("c0UpperShadow = " + name + " [" + value + "]");


            objArray = c0LowerShadow.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            System.out.println("c0LowerShadow = " + name + " [" + value + "]");
            //}


            // Retrieve fuzzy I/Os
            //  Object[] objArray;
            //  String name;
            //  double value;

            objArray = variation.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            outputVariation[j - maxBegIdx] = value;

            objArray = c0Body.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            inputC0Body[j - maxBegIdx] = value;

            objArray = c0BodyColour.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            inputC0BodyColour[j - maxBegIdx] = value;

            objArray = c0UpperShadow.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            inputC0UpperShadow[j - maxBegIdx] = value;

            objArray = c0LowerShadow.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            inputC0LowerShadow[j - maxBegIdx] = value;

            objArray = c0OpenStyle.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            inputC0OpenStyle[j - maxBegIdx] = value;

            objArray = c0CloseStyle.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];
            inputC0CloseStyle[j - maxBegIdx] = value;

            try {
                fuzzyRules.evaluateBlock();
            } catch (EvaluationException ex) {
                Logger.getLogger(PrepareData.class.getName()).log(Level.SEVERE, null, ex);
            }
            double patternValue = -999.0D;
            try {
                patternValue = pattern.defuzzify();
            } catch (NoRulesFiredException ex) {
                patternValue = 0.0D;
                //Logger.getLogger(PrepareData.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (patternValue > 0.0D) {
                patternCount++;
                System.out.println("Date: " + inputDate[j] + " - Pattern detected: " + CandlestickUtility.getName((int) Math.round(patternValue)) + " [" + patternValue + "].");
                patternIdList.add((int) Math.round(patternValue));
            }

        }

        if (patternCount > 0) {
            System.out.println(patternCount + " patterns detected.");
        } else {
            System.out.println("No patterns detected.");
        }


        //int globalSize = minEndIdx - maxBegIdx + 1;
        for (StockIndicator stockIndicator : stockIndicatorList) {
            if (stockIndicator.useAsInput()) {
                // Normalize valid values
                stockIndicator.normalize();
            }
        }

        NormalizeArray normArr;

        // Normalize set
        normArr = new NormalizeArray();
        double[] inputC0BodyColourNorm = normArr.process(inputC0BodyColour);
        // Normalize set
        normArr = new NormalizeArray();
        double[] inputC0BodyNorm = normArr.process(inputC0Body);
        // Normalize set
        normArr = new NormalizeArray();
        double[] inputC0UpperShadowNorm = normArr.process(inputC0UpperShadow);
        // Normalize set
        normArr = new NormalizeArray();
        double[] inputC0LowerShadowNorm = normArr.process(inputC0LowerShadow);
        // Normalize set
        normArr = new NormalizeArray();
        double[] inputC0OpenStyleNorm = normArr.process(inputC0OpenStyle);
        // Normalize set
        normArr = new NormalizeArray();
        double[] inputC0CloseStyleNorm = normArr.process(inputC0CloseStyle);
        // Normalize set

        NormalizeArray varNormArr = new NormalizeArray();
        double[] outputVariationNorm = varNormArr.process(outputVariation);

        System.out.println("Close length: " + inputClose.length);

        int inputIndicators = 0;
        for (StockIndicator stockIndicator : stockIndicatorList) {
            if (stockIndicator.useAsInput()) {
                inputIndicators++;
                System.out.println("This indicator is an input.");

                System.out.println(stockIndicator.getName() + ": ");
                System.out.println("Min: " + stockIndicator.getMinValue());
                System.out.println("Max: " + stockIndicator.getMaxValue());
                System.out.println("Begin Index: " + stockIndicator.getBegIdx());
                System.out.println("Length: " + stockIndicator.getLength());
                System.out.println("Values: " + stockIndicator.toString());
                System.out.println("Norm Values: " + stockIndicator.normValueToString());
            }
        }

        // Prepare the input data
        double[][] input = new double[maxLength][inputIndicators + 6];
        double[][] ideal = new double[maxLength][1];


        double[][] inputO = new double[maxLength][inputIndicators + 6];
        double[][] idealO = new double[maxLength][1];


        int inputIndicatorsUsed;
        for (int j = 0; j < maxLength; j++) {

            // Reset input indicators used count
            inputIndicatorsUsed = 0;

            for (int k = 0; k < stockIndicatorList.size(); k++) {

                if (inputIndicatorsUsed == inputIndicators) {
                    break;
                }

                StockIndicator stockIndicator = stockIndicatorList.get(k);
                if (stockIndicator.useAsInput()) {
                    input[j][inputIndicatorsUsed] = stockIndicator.getNormValues()[j];
                    inputIndicatorsUsed++;
                }
            }

            inputO[j][inputIndicatorsUsed] = inputC0BodyColour[j];
            inputO[j][inputIndicatorsUsed + 1] = inputC0Body[j];
            inputO[j][inputIndicatorsUsed + 2] = inputC0UpperShadow[j];
            inputO[j][inputIndicatorsUsed + 3] = inputC0LowerShadow[j];
            inputO[j][inputIndicatorsUsed + 4] = inputC0OpenStyle[j];
            inputO[j][inputIndicatorsUsed + 5] = inputC0CloseStyle[j];
            idealO[j][0] = outputVariation[j];

            input[j][inputIndicatorsUsed] = inputC0BodyColourNorm[j];
            input[j][inputIndicatorsUsed + 1] = inputC0BodyNorm[j];
            input[j][inputIndicatorsUsed + 2] = inputC0UpperShadowNorm[j];
            input[j][inputIndicatorsUsed + 3] = inputC0LowerShadowNorm[j];
            input[j][inputIndicatorsUsed + 4] = inputC0OpenStyleNorm[j];
            input[j][inputIndicatorsUsed + 5] = inputC0CloseStyleNorm[j];
            ideal[j][0] = outputVariationNorm[j];

            inputIndicatorsUsed += 6;

            String inputStr;
            inputStr = String.format("NN Original Data [" + j + "]: [%.2f, %.2f, %.2f, %.2f, %.2f, %.2f] -> [%.5f]", inputO[j][0], inputO[j][1], inputO[j][2], inputO[j][3], inputO[j][4], inputO[j][5], idealO[j][0]);
            System.out.println(inputStr);
            inputStr = String.format("NN Normalized Data [" + j + "]: [%.2f, %.2f, %.2f, %.2f, %.2f, %.2f] -> [%.5f]", input[j][0], input[j][1], input[j][2], input[j][3], input[j][4], input[j][5], ideal[j][0]);
            System.out.println(inputStr);
        }

        // Create training data set
        trainingSet = new BasicMLDataSet();

        // Create evaluate data set
        evaluationSet = new BasicMLDataSet();


        // Spread the data set, 50-50 between training and evaluation
        for (int j = 0; j < maxLength / 2; j++) {
            trainingSet.add(new BasicMLData(input[j]), new BasicMLData(ideal[j]));
        }

        for (int j = maxLength / 2; j < maxLength; j++) {
            evaluationSet.add(new BasicMLData(input[j]), new BasicMLData(ideal[j]));
        }

        // Randomly distribute up's and down's (this makes training/evaluation fair)
        Random random = new Random(System.currentTimeMillis());

        System.out.println("Training Set Size: " + trainingSet.size());
        System.out.println("Evaluation Set Size: " + evaluationSet.size());

        // Write the training data to a file
        //EncogUtility.saveEGB(new File(dataDir, Config.TRAINING_FILE), trainingSet);
        // Write the evaluation data to a file
        //EncogUtility.saveEGB(new File(dataDir, Config.EVALUATION_FILE), evaluationSet);

        System.out.println("Training input set size: " + trainingSet.getInputSize());
        System.out.println("Training ideal set size: " + trainingSet.getIdealSize());
        System.out.println("Evaluation input set size: " + evaluationSet.getInputSize());
        System.out.println("Evaluation ideal set size: " + evaluationSet.getIdealSize());

        // create a network
        BasicNetwork network = EncogUtility.simpleFeedForward(trainingSet.getInputSize(), Config.HIDDEN1_COUNT, Config.HIDDEN2_COUNT, trainingSet.getIdealSize(), true);

        // save the network and the training
        //EncogDirectoryPersistence.saveObject(new File(dataDir, Config.NETWORK_FILE), network);

        // Train the network
        trainNetwork(network, trainingSet, false);

        DecimalFormat format = new DecimalFormat("#0.0000");
        int count = 0;
        int correct = 0;


        int n = 0;
        double mse = 0.0D;
        double directionalMse = 0.0D;
        for (int j = maxLength / 2; j < maxLength; j++) {
            MLData predictData = network.compute(new BasicMLData(input[j]));
            System.out.println("predictData: " + predictData.getData(0));

            double _high = varNormArr.getStats().getNormalizedHigh();
            double _low = varNormArr.getStats().getNormalizedLow();
            double _max = varNormArr.getStats().getActualHigh();
            double _min = varNormArr.getStats().getActualLow();

            double result = ((_min - _max) * predictData.getData(0) - _high * _min + _max * _low) / (_low - _high);

            System.out.println("denormalized predictData: " + result + ", Date: " + inputDate[j + maxBegIdx]);

            variation.setInputValue(result);

            Object[] objArray;
            String name;
            double value;

            objArray = variation.translateFromMaxMF();
            name = (String) objArray[0];
            value = (Double) objArray[1];

            forecast[j + forwardStep] = inputClose[j + maxBegIdx] + inputClose[j + maxBegIdx] * (value / 100.D);

            System.out.println("variation: " + value + "%");
            String str = String.format("[%.2f] -> [%.2f]", inputClose[j + maxBegIdx], forecast[j + forwardStep]);
            System.out.println(str);


            if (j < maxLength - forwardStep) {
                n++;
                forecastError[j + forwardStep] = Math.abs(inputClose[j + forwardStep + maxBegIdx] - forecast[j + forwardStep]);
                mse += Math.pow(forecastError[j + forwardStep], 2);

                System.out.println(String.format("DIRECTION COMPARE: inputClose [%.2f], direction [%.2f] ? forecast [%.2f], direction [%.2f]", inputClose[j + forwardStep + maxBegIdx], getDirection(inputClose[j + forwardStep + maxBegIdx] - inputClose[j + maxBegIdx]), forecast[j + forwardStep], getDirection(forecast[j + forwardStep] - inputClose[j + maxBegIdx])));

                forecastDirectionError[j + forwardStep] = (getDirection(inputClose[j + forwardStep + maxBegIdx] - inputClose[j + maxBegIdx]) == getDirection(forecast[j + forwardStep] - inputClose[j + maxBegIdx])) ? 0 : 1;
                directionalMse += Math.pow(forecastDirectionError[j + forwardStep], 2);
            }
        }

        mse = mse / n;
        directionalMse = directionalMse / n;

        // Write chart data to file
        ChartData chartData = new ChartData(ticker.getSymbol(), inputDate, inputOpen, inputHigh, inputLow, inputClose, forecast, forecastError, forecastDirectionError, maxBegIdx, maxLength, macd, macdPeak, macdPeakScore, macdZeroScore, mfi, cci, mfiCciZeroScore);
        FileUtils.writeChartData(chartData, forwardStep);

        System.out.println("Commodity: " + ticker.getSymbol());
        System.out.println("MSE: " + mse + ", Directional MSE: " + directionalMse);

        for (int j = 0; j < forecastDirectionError.length; j++) {
            if (forecastDirectionError[j] == 0) {
                correct++;
            }
        }

        System.out.println("Correct Predictions: " + correct + " (out of a total " + forecastDirectionError.length + " predictions), Predictive correctness: " + (((double) correct / (double) forecastDirectionError.length) * 100.0D) + "%");
    }
}
