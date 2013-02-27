#!/bin/bash
./generate.sh $1 > output
grep 'DETECTION = 0.21' output | echo 'Bearish Engulfing:' `wc -l`
grep 'DETECTION = 0.21' output
grep 'DETECTION = 0.79' output | echo 'Bullish Engulfing:' `wc -l`
grep 'DETECTION = 0.79' output
