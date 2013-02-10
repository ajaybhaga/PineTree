package com.nnstockpredict.data;

import com.nnstockpredict.Utility.ArrayUtil;
import com.nnstockpredict.Utility.FileUtils;
import com.nnstockpredict.data.indicator.*;
import java.util.*;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.TickerSymbol;
import org.encog.ml.data.market.loader.LoadedMarketData;
import org.encog.ml.data.market.loader.MarketLoader;

public class PrepareData {

    /**
     * The serial id.
     */
    private static final long serialVersionUID = 170791819906003867L;
    /**
     * The loader to use to obtain the data.
     */
    private final MarketLoader loader;
    private MLDataSet trainingSet = null;
    private MLDataSet evaluationSet = null;

    public PrepareData(MarketLoader loader) {
        this.loader = loader;
    }

    public MLDataSet getTrainingSet() {
        return trainingSet;
    }

    public MLDataSet getEvaluationSet() {
        return evaluationSet;
    }

    /**
     * Load data from the loader.
     *
     * @param begin The beginning date.
     * @param end The ending date.
     */
    public void load(String symbol, final Date begin, final Date end) {
        loadSymbolData(new TickerSymbol(symbol), begin, end);
    }

    /**
     * Load one ticker symbol.
     *
     * @param ticker The ticker symbol to load.
     * @param from Load data from this date.
     * @param to Load data to this date.
     */
    private void loadSymbolData(TickerSymbol ticker, final Date from, final Date to) {
        final Collection<LoadedMarketData> data = loader.load(ticker,
                null, from, to);

        double[] inputHigh = new double[data.size()];
        double[] inputLow = new double[data.size()];
        double[] inputVolume = new double[data.size()];
        double[] inputOpen = new double[data.size()];
        double[] inputClose = new double[data.size()];
        double[] direction = new double[data.size()];
        Date[] inputDate = new Date[data.size()];

        int i = 0;
        for (final LoadedMarketData item : data) {
            inputHigh[i] = item.getData(MarketDataType.HIGH);
            inputLow[i] = item.getData(MarketDataType.LOW);
            inputVolume[i] = item.getData(MarketDataType.VOLUME);
            inputOpen[i] = item.getData(MarketDataType.OPEN);
            //inputClose[i] = item.getData(MarketDataType.ADJUSTED_CLOSE);
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

        List<StockIndicator> stockIndicatorList = new ArrayList<StockIndicator>(10);
       
        OpenPrice openPrice = new OpenPrice("Open Price", inputOpen.length);
        openPrice.setUseAsInput(true);
        openPrice.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(openPrice);

        HighPrice highPrice = new HighPrice("High Price", inputHigh.length);
        highPrice.setUseAsInput(true);
        highPrice.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(highPrice);

        LowPrice lowPrice = new LowPrice("Low Price", inputLow.length);
        lowPrice.setUseAsInput(true);
        lowPrice.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(lowPrice);
        
        ClosePrice closePrice = new ClosePrice("Close Price", inputClose.length);
        closePrice.setUseAsInput(true);
        closePrice.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(closePrice);

        RSI rsi = new RSI("RSI", inputClose.length);
        //rsi.setUseAsInput(true);
        rsi.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(rsi);

        Stoch stoch = new Stoch("Stoch %D", inputClose.length);
        //stoch.setUseAsInput(true);
        stoch.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(stoch);

        StochF stochF = new StochF("Stoch %K", inputClose.length);
        stochF.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(stochF);

        WilliamsR williamsR = new WilliamsR("Williams %R", inputClose.length);
        //williamsR.setUseAsInput(true);
        williamsR.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(williamsR);

        EMA5 ema5 = new EMA5("EMA 5", inputClose.length);
        ema5.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(ema5);

        EMA10 ema10 = new EMA10("EMA 10", inputClose.length);
        ema10.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(ema10);



        /*
         SMA10 sma10 = new SMA10("SMA 10", inputClose.length);
         sma10.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(sma10);
         WMA10 wma10 = new WMA10("WMA 10", inputClose.length);
         wma10.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
         stockIndicatorList.add(wma10);*/



        CCI cci = new CCI("CCI", inputClose.length);
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


        ADX adx = new ADX("ADX", inputClose.length);
        adx.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(adx);

        DIPlus diPlus = new DIPlus("DI Plus", inputClose.length);
        diPlus.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(diPlus);


        DIMinus diMinus = new DIMinus("DI Minus", inputClose.length);
        diMinus.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        stockIndicatorList.add(diMinus);

        MACD macd = new MACD("MACD", inputClose.length);
        macd.calculate(inputHigh, inputLow, inputVolume, inputOpen, inputClose);
        //macd.setUseAsInput(true);
        stockIndicatorList.add(macd);




        int maxBegIdx = 0;

        // Determine the common start index of all indicators
        for (StockIndicator stockIndicator : stockIndicatorList) {
            maxBegIdx = Math.max(maxBegIdx, stockIndicator.getBegIdx());
        }

        int maxLength = inputClose.length - maxBegIdx;

        // Put all scoring indicators last
        WilliamsRScore williamsRScore = new WilliamsRScore("Williams R Score", maxLength);
        williamsRScore.calculate(williamsR.getValues(), williamsR.getBegIdx(), maxBegIdx, maxLength);
        williamsRScore.setUseAsInput(true);
        stockIndicatorList.add(williamsRScore);

        ADXTrendScore adxTrendScore = new ADXTrendScore("ADX Trend Score", maxLength);
        adxTrendScore.calculate(adx.getValues(), adx.getBegIdx(), diPlus.getValues(), diPlus.getBegIdx(), diMinus.getValues(), diMinus.getBegIdx(), maxBegIdx, maxLength);
        adxTrendScore.setUseAsInput(true);
        stockIndicatorList.add(adxTrendScore);

        MACDPeakScore macdPeakScore = new MACDPeakScore("MACD Peak Score", maxLength);
        macdPeakScore.calculate(inputClose, macd.getValues(), macd.getBegIdx(), maxBegIdx, maxLength);
        macdPeakScore.setUseAsInput(true);
        stockIndicatorList.add(macdPeakScore);

        MACDZeroScore macdZeroScore = new MACDZeroScore("MACD Zero Score", maxLength);
        macdZeroScore.calculate(macd.getValues(), macd.getBegIdx(), maxBegIdx, maxLength);
        macdZeroScore.setUseAsInput(true);
        stockIndicatorList.add(macdZeroScore);

        ERSTrendScore ersTrendScore = new ERSTrendScore("ERS Trend Score", maxLength);
        ersTrendScore.calculate(ema5.getValues(), ema5.getBegIdx(), ema10.getValues(), ema10.getBegIdx(), rsi.getValues(), rsi.getBegIdx(), stochF.getValues(), stochF.getBegIdx(), stoch.getValues(), stoch.getBegIdx(), maxBegIdx, maxLength);
        ersTrendScore.setUseAsInput(true);
        stockIndicatorList.add(ersTrendScore);


        System.out.println("EMA5 = " + ema5.getMostRecentValue() + ", idx = " + (ema5.getLength() - 1));
        System.out.println("EMA10 = " + ema10.getMostRecentValue() + ", idx = " + (ema10.getLength() - 1));
        System.out.println("RSI = " + rsi.getMostRecentValue() + ", idx = " + (rsi.getLength() - 1));
        System.out.println("Stoch = " + stoch.getMostRecentValue() + ", idx = " + (stoch.getLength() - 1));
        System.out.println("Stoch F = " + stochF.getMostRecentValue() + ", idx = " + (stochF.getLength() - 1));
        System.out.println("ERS = " + ersTrendScore.getMostRecentValue() + ", idx = " + (ersTrendScore.getLength() - 1));

        System.out.println("Williams R = " + williamsR.getMostRecentValue());
        System.out.println("DI+ = " + diPlus.getMostRecentValue());
        System.out.println("DI- = " + diMinus.getMostRecentValue());

        System.out.println("MACDBegIdx = " + macd.getBegIdx());
        System.out.println("MACD Length = " + macd.getLength());

        System.out.println("Max begin index: " + maxBegIdx);

        System.out.println("maxBegIdx { inputDate: " + inputDate[maxBegIdx] + ", price: " + inputClose[maxBegIdx] + " }");
        System.out.println("maxLength { inputDate: " + inputDate[maxBegIdx + maxLength - 1] + ", price: " + inputClose[maxBegIdx + maxLength - 1] + " }");

        ChartData chartData = new ChartData(ticker.getSymbol(), inputDate, inputOpen, inputHigh, inputLow, inputClose, maxBegIdx, maxLength, macd, macdPeakScore, macdZeroScore);        
        FileUtils.writeChartData(chartData);
        
        //int globalSize = minEndIdx - maxBegIdx + 1;
        for (StockIndicator stockIndicator : stockIndicatorList) {
            if (stockIndicator.useAsInput()) {
                // Normalize valid values
                stockIndicator.normalize();
            }
        }

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
        double[][] input = new double[maxLength][inputIndicators];
        double[][] ideal = new double[maxLength][1];


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

            ideal[j][0] = direction[maxBegIdx + j];

            System.out.println("direction[" + (maxBegIdx + j) + "]=" + direction[maxBegIdx + j]);
        }

        // Sort and separate training and evaluation data
        ArrayList<DataWindow> upList = new ArrayList<DataWindow>(maxLength);
        ArrayList<DataWindow> downList = new ArrayList<DataWindow>(maxLength);


        int days = 4;
        for (int j = 0; j < maxLength - (days - 1); j++) {

            BasicMLData[] windowArray = new BasicMLData[days];

            double d = 0.0;

            windowArray[0] = new BasicMLData(Arrays.copyOf(input[j], input[j].length));

            for (int k = 1; k <= (days - 1); k++) {
                windowArray[k] = new BasicMLData(Arrays.copyOf(input[j + k], input[j + k].length));
                d = ideal[j + k][0];
            }

            // Set data window
            DataWindow dataWindow = new DataWindow(windowArray);
            // Set direction for window
            dataWindow.setDirection(d);

            if (d == 1.0) {
                upList.add(dataWindow);
            } else {
                downList.add(dataWindow);
            }
            
            
            System.out.println("Day #" + (j+1) + ": " + dataWindow + ", direction = " + d);           
        }
        
   
        // Create training data set
        trainingSet = new BasicMLDataSet();

        // Create evaluate data set
        evaluationSet = new BasicMLDataSet();

        // Randomly distribute up's and down's (this makes training/evaluation fair)
        Random random = new Random(System.currentTimeMillis());

        int j1 = 0;
        int j2 = 0;
        int w = 0;

        while ((upList.size() > 0) || (downList.size() > 0)) {

            if (upList.size() > 0) {
                j1 = random.nextInt(upList.size());
            }

            if (downList.size() > 0) {
                j2 = random.nextInt(downList.size());
            }

            w = (w == 1) ? 0 : 1;

            if (w == 0) {
                if (upList.size() > 0) {
                    DataWindow dataWindow = upList.remove(j1);
                    trainingSet.add(new BasicMLDataPair(dataWindow.getData(), new BasicMLData(dataWindow.getDirectionArray())));
                }

                if (downList.size() > 0) {
                    DataWindow dataWindow = downList.remove(j2);
                    trainingSet.add(new BasicMLDataPair(dataWindow.getData(), new BasicMLData(dataWindow.getDirectionArray())));

                }
            } else {
                if (upList.size() > 0) {
                    DataWindow dataWindow = upList.remove(j1);
                    evaluationSet.add(new BasicMLDataPair(dataWindow.getData(), new BasicMLData(dataWindow.getDirectionArray())));

                }

                if (downList.size() > 0) {
                    DataWindow dataWindow = downList.remove(j2);
                    evaluationSet.add(new BasicMLDataPair(dataWindow.getData(), new BasicMLData(dataWindow.getDirectionArray())));

                }
            }
        }

        System.out.println("Training Set Size: " + trainingSet.size());
        System.out.println("Evaluation Set Size: " + evaluationSet.size());
    }
}
