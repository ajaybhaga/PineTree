/*
 * Encog(tm) Java Examples v3.2
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/

 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information on Heaton Research copyrights, licenses
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package com.bhaga.pinetree.nn;

import com.bhaga.pinetree.nn.Utility.loader.EODFinanceLoader;
import com.bhaga.pinetree.nn.data.NNConfigData;
import com.bhaga.pinetree.nn.data.PrepareData;
import com.bhaga.pinetree.nn.exception.NoDataException;
import com.bhaga.pinetree.ui.Progress;
import com.bhaga.pinetree.ui.data.DataProvider;
import com.bhaga.pinetree.nn.data.ResultData;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.soap.SOAPException;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.market.loader.MarketLoader;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;

/**
 * Build the training data for the prediction and store it in an Encog file for
 * later training.
 *
 * @author jeff
 *
 */
public class MarketBuildTraining {

    /**
     * Create a an elman neural network.
     *
     * @param input The number of input neurons.
     * @param hidden1 The number of hidden layer 1 neurons.
     * @param hidden2 The number of hidden layer 2 neurons.
     * @param output The number of output neurons.
     * @param tanh True to use hyperbolic tangent activation function, false to
     * use the sigmoid activation function.
     * @return The neural network.
     */
    public static BasicNetwork createElmanNetwork(final int input,
            final int hidden1, final int hidden2, final int output,
            final boolean tanh) {
        final ElmanPattern pattern = new ElmanPattern();
        pattern.setInputNeurons(input);
        pattern.setOutputNeurons(output);
        if (tanh) {
            pattern.setActivationFunction(new ActivationTANH());
        } else {
            pattern.setActivationFunction(new ActivationSigmoid());
        }

        if (hidden1 > 0) {
            pattern.addHiddenLayer(hidden1);
        }
        if (hidden2 > 0) {
            pattern.addHiddenLayer(hidden2);
        }

        final BasicNetwork network = (BasicNetwork) pattern.generate();
        network.reset();
        return network;
    }

    public static void generate(String symbol, String exchange, File dataDir, int timeInterval, ResultData resultData, NNConfigData nnConfigData, Progress progress) throws SOAPException, IOException, MalformedURLException, ParseException, NoDataException {

        // Retrieve stock data from EOD web service
        final EODFinanceLoader loader = new EODFinanceLoader();

        Calendar end = new GregorianCalendar();// end today
        Calendar begin = (Calendar) end.clone();

        // Set time interval of result data
        resultData.setTimeInterval(timeInterval);
        
        int calendarType = 0;
        if (timeInterval == DataProvider.Config.TIME_INTERVAL_DAILY) {
            begin.add(Calendar.YEAR, -2);
            begin.add(Calendar.DATE, -364);
            calendarType = Calendar.DATE;
        } else if (timeInterval == DataProvider.Config.TIME_INTERVAL_HOURLY) {
            begin.add(Calendar.DATE, -30);
            calendarType = Calendar.HOUR_OF_DAY;
        }

        // Prepare data inputs
        PrepareData prepareData = new PrepareData(loader, dataDir, calendarType, resultData, nnConfigData, progress);
        prepareData.load(symbol, exchange, begin.getTime(), end.getTime(), 1);
        prepareData.load(symbol, exchange, begin.getTime(), end.getTime(), 2);
        prepareData.load(symbol, exchange, begin.getTime(), end.getTime(), 4);
    }
}
