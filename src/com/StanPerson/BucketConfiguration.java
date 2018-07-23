package com.StanPerson;

import java.util.ArrayList;

public class BucketConfiguration {


private ArrayList<InvestmentAllocation> allocations;

public BucketConfiguration() {
    allocations = new ArrayList<>();

}
public void add(String csvItem) {
    InvestmentAllocation ia = new InvestmentAllocation();

    String[] values = csvItem.split(",") ;
    ia.setTicker(values[0]);
    ia.setDescription( values[1]);
    // these percentages are the percent of the over-all portfolio value.
    // if the investment is allocated to more than one bucket, then have to compute the percentage allocation
    // if an investment is in bucket1, then it is never split (at least for now)
    Double bucket1Pct = Double.parseDouble(values[2]);
    Double bucket2Pct = Double.parseDouble(values[3]);
    Double bucket3Pct = Double.parseDouble(values[4]);
    Double bucket2InvAllocation = 100.0;
    Double bucket3InvAllocation = 100.0;
    boolean split = false;
    if ((bucket2Pct > 0.0) && (bucket3Pct > 0.0)) {
        bucket2InvAllocation = bucket2Pct/(bucket2Pct + bucket3Pct);
        bucket3InvAllocation = bucket3Pct/(bucket2Pct + bucket3Pct);
        split = true;
    }

    ia.setBucket1Pct(bucket1Pct);
    ia.setBucket2Pct(bucket2Pct);
    ia.setBucket3Pct(bucket3Pct);
    ia.setSplit(split);
    ia.setBucket2AllocationPct(bucket2InvAllocation);
    ia.setBucket3AllocationPct(bucket3InvAllocation);
    allocations.add(ia);
}

public InvestmentAllocation get(String symbol) {
    for (InvestmentAllocation inv: allocations ){
        if (inv.getTicker().equalsIgnoreCase(symbol)) return inv;
        }
        return null;
}

public void print(){
    System.out.println("Bucket Configuration: ");
    for (InvestmentAllocation ia: allocations) {
        System.out.println(ia.toString());
    }
}

}
