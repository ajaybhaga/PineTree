#!/bin/bash
rm tmp.output
./generate.sh $1 >> tmp.output
echo $1 >> summary.output
grep 'Correct Predictions' tmp.output >> summary.output
