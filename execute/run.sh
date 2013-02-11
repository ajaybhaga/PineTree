#!/bin/bash
./update.sh
./generate.sh $1 > generate.out
./train.sh $1 > train.out
./evaluate.sh $1 >> train.out
