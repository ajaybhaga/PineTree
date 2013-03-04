#!/bin/bash
for STOCK in UPW XTN RYU VPU IDU PUI XLU FXU JXI DBU
do
  echo Analyzing $STOCK...
  ./test_stock.sh $STOCK
done
