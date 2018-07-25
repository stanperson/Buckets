package com.StanPerson;

import java.util.ArrayList;

public class BucketConfiguration {


private ArrayList<InvestmentAllocation> allocations;
private ArrayList<Double> bucketSizes;

public BucketConfiguration() {
    allocations = new ArrayList<>();
    bucketSizes = new ArrayList<>();  //high level total bucket size as a percentage of the portfolio. e.g. 10,55, 35.
}
public void add(String csvItem) {
    InvestmentAllocation ia = new InvestmentAllocation();

    String[] values = csvItem.split(",") ;
    String value0= values[0].toLowerCase();
    switch (value0) {
        case "end":
            return;

        case "":
            return;

        case "buckets":
            Double total = 0.;
            bucketSizes.add(0, Double.parseDouble(values[3]));
            bucketSizes.add(1, Double.parseDouble(values[4]));
            bucketSizes.add(2, Double.parseDouble(values[5]));
            total += Double.parseDouble(values[3]);
            total += Double.parseDouble(values[4]);
            total += Double.parseDouble(values[5]);
            if (total != 100.0) {
                System.out.println("Total should be 100%: " + 100.);
            }
            break;

        default:
            ia.setTicker(values[0]);
            ia.setInvClass(values[1]);
            ia.setDescription(values[2]);
            // these percentages are the percent of the over-all portfolio value.
            // if the investment is allocated to more than one bucket, then have to compute the percentage allocation
            // if an investment is in bucket1, then it is never split (at least for now)
            Double bucket1Pct = Double.parseDouble(values[3]);
            Double bucket2Pct = Double.parseDouble(values[4]);
            Double bucket3Pct = Double.parseDouble(values[5]);
            Double bucket2InvAllocation = 100.0;
            Double bucket3InvAllocation = 100.0;
            boolean split = false;
            if ((bucket2Pct > 0.0) && (bucket3Pct > 0.0)) {
                bucket2InvAllocation = bucket2Pct / (bucket2Pct + bucket3Pct);
                bucket3InvAllocation = bucket3Pct / (bucket2Pct + bucket3Pct);
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
}

public InvestmentAllocation get(String symbol) {
    for (InvestmentAllocation inv: allocations ){
        if (inv.getTicker().equalsIgnoreCase(symbol)) return inv;
        }
        return null;
}

public ArrayList<InvestmentAllocation> getAllocations() {
    return allocations;
}
public Double getBucketFractionOfPortfolio(int bucketId) {
    // return a fraction rather than a percent
    return bucketSizes.get(bucketId)/100.0;
}
public void print(){
    System.out.println("Bucket Configuration: ");
    for (InvestmentAllocation ia: allocations) {
        System.out.println(ia.toString());
    }
}

}
