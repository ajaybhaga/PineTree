#!/bin/bash
for STOCK in UPW XTN RYU VPU IDU PUI XLU FXU JXI DBU XHS AXHE FXH CURE RXL HGEM PJP VHT IHE IBB PXE UGA PXI MLPL BNO CGW AMJ AMLP RYE DIG URTH VNQI ACCU BJK TZW CARZ RXI AADR IGEM COLX
do
  echo Analyzing $STOCK...
  ./test_stock.sh $STOCK
done
