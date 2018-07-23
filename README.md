# Buckets

Simple portfolio-bucketizer based on an CSV input file produced from Fidelity. It supports 3 buckets,
the first for cash, the second for 2-5 years out and the third long term equity positions.

It expects a Portfolio Plan file that lists bucket sizes as a percentage of the over-all portfolio.
For now, there are 3 buckets, that must total 100% (at least 'should' total). The other rows in
the file are one per investment, with a ticker symbol, description, and three bucket allocation percentages.

The output (to stdout) looks like:

`Portfolio Size: 100029.28

Cost Basis: 5644.20

Unrealized Profits: 4385.08


Bucket1: $xxxx Portfolio %: yy Target: 12.0 Out of balance by: -$zzz

 symbol: $xxxx Gain: $-0.56 Portfolio %: 0.87 Target%: 0.00 Out of Balance:  
 
Bucket2: $bbbbb Portfolio %: 32.61 Target: 50.0
 STIP: $zzz Gain: $xx Portfolio %: 11.70 Target%: 12.50 Out of Balance: -xxx Shares: -nn
 FLTB: $aaa Gain: $yy Portfolio %: 4.29 Target%: 7.50   Out of Balance: -xxx Shares: -mm`

and so on for each investment and each bucket.