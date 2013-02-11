#!/bin/bash
java -Xmx1024MB -jar NNStockPredictTest.jar ./data $1 train $2
