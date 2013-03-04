#!/bin/bash
./generate.sh $1 | grep MSE > $1_MSE.output
