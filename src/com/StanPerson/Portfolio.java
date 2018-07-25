package com.StanPerson;

import java.util.ArrayList;
import java.util.List;

import static com.StanPerson.Buckets.bucketConfiguration;
import static com.StanPerson.Buckets.portfolio;

// remove test git integration.


public class Portfolio {


    private String dateDownLoaded="undefined";
    private List<Investment> investments = new ArrayList<>();
    private Bucket bucket1 = new Bucket("1");
    private Bucket bucket2 = new Bucket("2");
    private Bucket bucket3 = new Bucket("3");
    private Bucket annuity  = new Bucket("4");
    private Bucket unBucketed = new Bucket("5");
    private Double portfolioSize = 0.0;
    private Double portfolioBasis= 0.0;
    private Double cashAllocation = 0.0;
    private Double equityAllocation = 0.0;
    private Double fixedIncomeAllocation = 0.0;
    private Double unAllocated = 0.0;


    public void add(Investment investment) {
        // check for duplicates...I'm saying that if I have the same investment in two accounts, then it's
        // for the same bucket. I guess. :)
        String ticker = investment.getSymbol();
        Investment inv2 = this.get(ticker);         // look in this portfolio and see if there is an investment of same ticker alreaday there.

        if ( inv2 != null ) {
            // there is a matching ticker in the portfolio. Merge the two investments.
            inv2.merge(investment);

        } else {
            // classify the investment as to cash, equity or fixed income.
            InvestmentAllocation ia = bucketConfiguration.get(ticker);
            if (ia == null) {
                investment.setInvClass("Cash");
            } else {
                investment.setInvClass(ia.getInvClass());
            }
            investments.add(investment);    // this is the first of its ticker value.
        }
    }

    public Investment get(int index) {
       return investments.get(index);

    }

    public List<Investment> getInvestments() {
        return investments;
    }
    public String getDateDownLoaded() {
        return dateDownLoaded;
    }

    public void setDateDownLoaded(String dateDownLoaded) {
        this.dateDownLoaded = dateDownLoaded;
    }

    public void bucketize(BucketConfiguration bucketConfiguration) {


        for (Investment inv: investments) {

            String symbol = inv.getSymbol();
            Double price  = inv.getPrice();

            portfolioSize += inv.getCurrValue();
            portfolioBasis += inv.getCostBasis();

            // classify the investment
            String iClass = inv.getInvClass();
            switch (iClass){
                case "Cash":
                    cashAllocation+= inv.getCurrValue();
                    break;
                case "Equity":
                    equityAllocation+=inv.getCurrValue();
                    break;
                case "Fixed":
                    fixedIncomeAllocation+=inv.getCurrValue();
                    break;
                default:
                    unAllocated+=inv.getCurrValue();
            }


            if (inv.getInvClass().equalsIgnoreCase("Cash")) {

                bucket1.add(inv);
            } else {
                // find the symbol in the bucket configuration
                InvestmentAllocation ia = bucketConfiguration.get(symbol);
                if (ia != null) {
                    if (!ia.isSplit()){
                        if (ia.getBucket1Pct() > 0.0) { // theoretically something like MINT could go in bucket 1.
                            inv.setTargetPct(ia.getBucket1Pct());
                            bucket1.add(inv);

                        } else if (ia.getBucket2Pct() > 0.0) {
                            inv.setTargetPct(ia.getBucket2Pct());

                            bucket2.add(inv);
                        } else if (ia.getBucket3Pct() > 0.0) {
                            inv.setTargetPct(ia.getBucket3Pct());
                            bucket3.add(inv);
                        }
                        else {
                            // stick it in bucket4, the unbucketed bucket.
                            // all bucket pct values are 0...
                            inv.setTargetPct(0.0);
                            unBucketed.add(inv);
                        }

                    } else {
                        // this investment is split between buckets 2 and 3

                        //inv will be used for bucket2, its clone will be used for bucket3

                        Investment clone = new Investment(inv);
                        clone.sell(ia.getBucket3AllocationPct());
                        inv.sell(ia.getBucket2AllocationPct()); // sell takes a 'fraction' not a pct. so something like .5655 not 56.5%
                        inv.setTargetPct(ia.getBucket2Pct());
                        clone.setTargetPct(ia.getBucket3Pct());
                        bucket2.add(inv);
                        bucket3.add(clone);

                    }


                } else {
                    inv.setTargetPct(0.0);
                    unBucketed.add(inv);

                }

            }
        }

    }



    public  void validate(BucketConfiguration bucketConfiguration) {
        // verify that everything in the portfolio plan is also in the portfolio.
        // Thus far, the portfolio content is determined by the file from Fidelity (the account info). The plan may have things not in currently in
        // the Fidelity Portfolio.

        ArrayList<InvestmentAllocation> alls = bucketConfiguration.getAllocations();
        for (InvestmentAllocation allocation : alls) {
            String allocationTicker = allocation.getTicker();
            Investment i = portfolio.get(allocationTicker);
            if (portfolio.get(allocationTicker) == null) {
                // the portfolio does not have anything for this allocation
                System.out.println("adding investment for : " + allocationTicker);
                Investment newInv = new Investment();
                newInv.setSymbol(allocationTicker);
                newInv.setDescription(allocation.getDescription());
                if (allocation.isSplit()) {
                    System.out.println("Can't handle split defaults from allocations.Pick a bucket." + allocation.toString());
                } else {
                    //find first non zero bucket percentage.
                    int bucketId = 1;
                    Double bucketPct = allocation.getBucket1Pct();
                    if (bucketPct == 0.) {
                        bucketPct = allocation.getBucket2Pct();
                        bucketId++;
                        if (bucketPct == 0.) {
                            bucketPct = allocation.getBucket3Pct();
                            bucketId++;
                        }
                    }
                    newInv.setTargetPct(bucketPct);
                    portfolio.add(newInv);
                }


            }

        }
    }

    public void print() {
        for (Investment inv: investments) {
            System.out.println("   " + inv.toString());
        }
    }
    public String toString(){
        String returnString = "";
        for (Investment inv: investments) {
            returnString += inv.toString();
        }
        return returnString;
    }

    public void printBuckets(BucketConfiguration bucketConfiguration) {
        Double sizeWOAnnuity = portfolioSize - annuity.getBucketSize();

        System.out.println("Date downloaded: " + dateDownLoaded);
        System.out.println ("Total Portfolio Size: $" + String.format("%.2f", portfolioSize));
        System.out.println ("Cost Basis:           $" + String.format("%.2f", portfolioBasis));
        System.out.println ("Unrealized Profits:   $" + String.format("%.2f", (portfolioSize -portfolioBasis)));
        System.out.println ("Cash Allocation:      $" + String.format("%.2f", (cashAllocation)) + " Portfolio%: " + String.format("%.1f", 100.* cashAllocation/portfolioSize));
        System.out.println ("Fixed Income Alloc:   $" + String.format("%.2f", (fixedIncomeAllocation)) + " Portfolio%: " + String.format("%.1f", 100.* fixedIncomeAllocation/portfolioSize));
        System.out.println ("Equity Allocation:    $" + String.format("%.2f", (equityAllocation))+ " Portfolio%: " + String.format("%.1f", 100.* equityAllocation/portfolioSize));
        System.out.println ("Not Allocated:        $" + String.format("%.2f", (unAllocated)));
        System.out.println ("*");
        System.out.println (" Bucket1 (Cash):      $" + String.format("%.2f", bucket1.getBucketSize()) + " Portfolio %: " + String.format("%.2f", 100 * bucket1.getBucketSize()/sizeWOAnnuity)
                + " Target: " + String.format("%.1f", 100.* bucketConfiguration.getBucketFractionOfPortfolio(0))
                + " Out of balance by: "+ String.format("%.2f", (bucketConfiguration.getBucketFractionOfPortfolio(0) * sizeWOAnnuity) - bucket1.getBucketSize()) );
        bucket1.printBucket(sizeWOAnnuity);
        System.out.println ("*");
        System.out.println (" Bucket2 (Income):    $" + String.format("%.2f", bucket2.getBucketSize()) + " Portfolio %: " + String.format("%.2f", 100 * bucket2.getBucketSize()/sizeWOAnnuity) + " Target: " + String.format("%.1f",100* bucketConfiguration.getBucketFractionOfPortfolio(1) ));
        bucket2.printBucket(sizeWOAnnuity);
        System.out.println ("*");
        System.out.println (" Bucket3 (Long Term): $ " + String.format("%.2f", bucket3.getBucketSize()) + " Portfolio %: " + String.format("%.2f", 100 * bucket3.getBucketSize()/sizeWOAnnuity) + " Target:  " + String.format("%.1f",100* bucketConfiguration.getBucketFractionOfPortfolio(2)));
        bucket3.printBucket(sizeWOAnnuity);
        System.out.println ("*");
        System.out.println (" Unbucketed:          $" + String.format("%.2f", unBucketed.getBucketSize()));
        unBucketed.printBucket(sizeWOAnnuity);
        System.out.println ("*");
        System.out.println (" Annuity:             $" + String.format("%.2f", annuity.getBucketSize()));
        System.out.println ("*");
        Double totalBucketSize = + bucket1.getBucketSize()+ bucket2.getBucketSize() + bucket3.getBucketSize() + annuity.getBucketSize();

        //System.out.println ("sum of bucket sizes: " + String.format("%.2f", totalBucketSize ));

    }

    public Investment get(String symbol) {
        for (Investment inv: investments )
            if (inv.getSymbol().equalsIgnoreCase(symbol)) {

                return inv;
            }

        return null;
    }

    public Double getCashAllocation() {
        return cashAllocation;
    }

    public void setCashAllocation(Double cashAllocation) {
        this.cashAllocation = cashAllocation;
    }

    public Double getEquityAllocation() {
        return equityAllocation;
    }

    public void setEquityAllocation(Double equityAllocation) {
        this.equityAllocation = equityAllocation;
    }

    public Double getFixedIncomeAllocation() {
        return fixedIncomeAllocation;
    }

    public void setFixedIncomeAllocation(Double fixedIncomeAllocation) {
        this.fixedIncomeAllocation = fixedIncomeAllocation;
    }

    public void del(String symbol) {
        for (Investment inv: investments) {
            if (inv.getSymbol().equalsIgnoreCase(symbol)) {
                investments.remove(inv);
                break;
            }
        }
    }
}
