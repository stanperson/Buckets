package com.StanPerson;

import java.util.ArrayList;
import java.util.List;

public class Bucket {

    private String bucketId; // one of 1,2,3,Annuity
    private List<Investment> inv= new ArrayList<Investment>();  // the investments allocated to this bucket
    private Double bucketSize= 0.0;

    public Bucket(String Id) {
        bucketId = Id;

    }

    public void add(Investment inv) {
        this.inv.add(inv);
        bucketSize += inv.getCurrValue();
    }

    public void printBucket(Double sizeWOAnnuity){
        Double totalOOB = 0.0;
        Double bucketTarget = 0.0;
        for (Investment investment: inv) {
            Double actualPct = 100.*investment.getCurrValue()/sizeWOAnnuity;
            Double targetPct = investment.getTargetPct();
            bucketTarget += targetPct;
            Double currValue = investment.getCurrValue();
            Double outOfBalance = 0.0;
            Double appropriateValue =0.0;
            Double shares = 0.0;
            String shareTxt = "";
            if (targetPct > 0.0) {
                outOfBalance = currValue - targetPct * sizeWOAnnuity / 100.;
                appropriateValue = currValue + outOfBalance;
                totalOOB += outOfBalance;
                if (investment.getPrice() > 0.0) {
                    shares = outOfBalance / investment.getPrice();
                    shareTxt = String.format ("%.2f", shares);
                } else {
                    shareTxt = "Unpriced";
                }
            }


            System.out.println("++++" + investment.getSymbol() + ": $" + String.format("%.2f", currValue)
                                      + " Gain: $" + String.format("%.2f",currValue-investment.getCostBasis())
                                      + " Portfolio %: " + String.format("%.2f",actualPct) + " Target%: " + String.format("%.2f",targetPct)
                                      + " Out of Balance: " + String.format("%.2f",outOfBalance) + " Shares: " + shareTxt + " Appropriate value: " + String.format("%.2f", appropriateValue));

        }
        if (bucketTarget != 0.0)
            System.out.println("Sum of target %: " + String.format("%.2f", bucketTarget));
        if (totalOOB != 0.0)
            System.out.println("Bucket total out of balance: " + String.format("%.2f", totalOOB));
    }

    public String getBucketId() {
        return bucketId;
    }

    public List<Investment> getInv() {
        return inv;
    }

    public Double getBucketSize() {
        return bucketSize;
    }
}
