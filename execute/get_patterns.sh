#!/bin/bash
./generate.sh $1 > output
grep 'Pattern detected' output
grep 'patterns' output
