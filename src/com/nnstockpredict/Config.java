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
package com.nnstockpredict;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.encog.ml.data.market.TickerSymbol;

/**
 * Basic config info
 *
 * @author Ajay Bhaga
 *
 */
public class Config {

    public static final String TRAINING_FILE = "trainingData.egb";
    public static final String EVALUATION_FILE = "evaluationData.egb";
    public static final String NETWORK_FILE = "marketNetwork.eg";
    // 74,53 = 57% FAS
    // 74,53 = 60% TNA
    // 90,0 = 57% FAS
    // 90,0 = 58-60% TNA
    // 90,0 = 54% TZA
    // 90,0 = 60% AAPL   
    // 80,10 = 61% TNA
    // 80,20 = 61.9% TNA
    // 40,40 = 61.7% TNA
    // 90,20 = 61.9329% TNA
    // 90,20 = 61.3636% TQQQ
    
    // 2 indicators (MACD,RSI) (90,20), 64.3059% TQQQ
    // 3 indicators (MACD, MACD peak, RSI), (90, 0), 67.9887% 35% error TQQQ    
    
    
    // (RSI,STOCH K, STOCH D,MACD,ATR,MACD peak), (90, 0) = 70.339% TQQQ
    
    // Prune: 21, 12
    public static final int HIDDEN1_COUNT = 42;
    public static final int HIDDEN2_COUNT = 0;
    public static final int TRAINING_DATA_SET_PERCENTAGE = 50;
    public static final int EVALUATE_DATA_SET_PERCENTAGE = 100 - TRAINING_DATA_SET_PERCENTAGE;
}
