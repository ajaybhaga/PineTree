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

import java.io.File;
import java.text.DecimalFormat;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

public class MarketEvaluate {

    enum Direction {

        up, down
    };

    public static Direction determineDirection(double d) {
        if (d < 0) {
            return Direction.down;
        } else {
            return Direction.up;
        }
    }

    public static void test(MLDataSet testSet, BasicNetwork network) {
        DecimalFormat format = new DecimalFormat("#0.0000");
        int count = 0;
        int correct = 0;
        for (MLDataPair pair : testSet) {
            MLData input = pair.getInput();
            MLData actualData = pair.getIdeal();
            MLData predictData = network.compute(input);

            double actual = actualData.getData(0);
            double predict = predictData.getData(0);
            double diff = Math.abs(predict - actual);

          //  Direction actualDirection = determineDirection(actual);
          //  Direction predictDirection = determineDirection(predict);

          //  if (actualDirection == predictDirection) {
          //      correct++;
          //  }     
            
            // forecast[i+1] = close[i] + close[i](c/100);
            
            
            double threshold = 0.05D;
           // if (diff < threshold) {
            if ((actual >= 0) == (predict >= 0)) {
                correct++;
            }

            count++;
            
            String inputStr = String.format("[%.2f, %.2f, %.2f, %.2f, %.2f, %.2f]", input.getData()[0], input.getData()[1], input.getData()[2], input.getData()[3], input.getData()[4], input.getData()[5]);

            System.out.println("Day " + count + ": inputStr=" + inputStr + ", actual="
                    + format.format(actual)
                    + ", predict=" + format.format(predict) + ", diff=" + diff);
        }

        System.out.println("Test Set Summary:");
        double percent = (double) correct / (double) count;
        System.out.println("Correct predictions:" + correct + "/" + count);
        System.out.println("Prediction Accuracy:"
                + format.format(percent * 100) + "%");

    }

    public static void evaluate(File dataDir) {

        final File networkFile = new File(dataDir, Config.NETWORK_FILE);
        final File trainingFile = new File(dataDir, Config.TRAINING_FILE);
        final File evaluationFile = new File(dataDir, Config.EVALUATION_FILE);

        // network file
        if (!networkFile.exists()) {
            System.out.println("Can't read file: " + networkFile.getAbsolutePath());
            return;
        }

        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(networkFile);

        // evaluation file
        if (!evaluationFile.exists()) {
            System.out.println("Can't read file: " + evaluationFile.getAbsolutePath());
            return;
        }

        final MLDataSet trainingSet = EncogUtility.loadEGB2Memory(trainingFile);
        final MLDataSet evaluationSet = EncogUtility.loadEGB2Memory(evaluationFile);

        // Test the training set
        test(trainingSet, network);
        // Test the evaluation set
        test(evaluationSet, network);
        
        
        // Test and predict for tomorrow
        /*
        MLData input = pair.getInput();
            MLData actualData = pair.getIdeal();
            MLData predictData = network.compute(input);

            double actual = actualData.getData(0);
            double predict = predictData.getData(0);
            double diff = Math.abs(predict - actual);

            Direction actualDirection = determineDirection(actual);
            Direction predictDirection = determineDirection(predict);

            if (actualDirection == predictDirection) {
                correct++;
            }

            count++;

            System.out.println("Day " + count + ":actual="
                    + format.format(actual) + "(" + actualDirection + ")"
                    + ",predict=" + format.format(predict) + "("
                    + predictDirection + ")" + ",diff=" + diff);*/
    }
}
