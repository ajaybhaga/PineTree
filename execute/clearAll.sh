#!/bin/bash
ps -ef | grep run.sh | tr -s ' ' ':' | cut -d":" -f3 | xargs kill
