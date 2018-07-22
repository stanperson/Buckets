package com.StanPerson;

import java.util.ArrayList;
import java.util.List;

// comment to test git integration.


public class Portfolio {

    private List<Investment> investments;
    private Bucket bucket1 = new Bucket("1");
    private Bucket bucket2 = new Bucket("2");
    private Bucket bucket3 = new Bucket("3");
    private Bucket annuity  = new Bucket("4");
    private Double portfolioSize = 0.0;
    private Double portfolioBasis= 0.0;

    public Portfolio()

    {

        investments = new ArrayList<>();
        // put some stub investments into the portfolio
        Investment inv = new Investment();
        inv.setSymbol("FNMIX");
        inv.setDescription("Fidelity New Markets Income Fund");
        investments.add(inv);
        inv.setTargetPct(2.5);
        inv = new Investment();
        inv.setSymbol("FAGIX");
        inv.setDescription("Fidelity Capital & Income Fund");
        investments.add(inv);
        inv.setTargetPct(2.5);
        inv = new Investment();
        inv.setSymbol("FFHRX");
        inv.setDescription("Fidelity Floating Rate High Income Fund");
        investments.add(inv);
        inv.setTargetPct(7.5);
        System.out.println(investments.toString());
    }

    public void add(Investment investment) {
        // check for duplicates...I'm saying that if I have the same investment in two accounts, then it's
        // for the same bucket. I guess. :)
        String ticker = investment.getSymbol();
        Investment inv2 = this.get(ticker);
        String ticker2 = "";
        if ( inv2 != null ) {
            // there is a matching symbol in the set. Merge the two investments.
            System.out.println("Merging");
            System.out.println(investment.toString());
            System.out.println(inv2.toString());
            System.out.println("Merged");
            System.out.println((inv2.merge(investment)).toString());

        } else
            investments.add(investment);

    }

    public Investment get(int index) {
       return investments.get(index);

    }

    public void bucketize() {


        for (Investment inv: investments) {

            String symbol = inv.getSymbol();
            Double price  = inv.getPrice();

            portfolioSize += inv.getCurrValue();
            portfolioBasis += inv.getCostBasis();

            // assuming that all cash investments have a current price of $1.00 (true for money market(mostly) and CDs)
            if (price == 1.0) {
                bucket1.add(inv);
            } else {
                switch (symbol) {
                    case "FLTB" :
                        inv.setTargetPct(7.5);
                        bucket2.add(inv);
                        break;
                    case "ITOT":
                        inv.setTargetPct(10.);
                        bucket3.add(inv);
                        break;
                    case "IUSB":
                        inv.setTargetPct(15.0);
                        bucket2.add(inv);
                        break;
                    case "IXUS":
                        inv.setTargetPct(8.0);   // this plus VEU is 9%
                        bucket3.add(inv);
                        break;
                    case "STIP":
                        inv.setTargetPct(12.5);  // this plus VTIP is 20%
                        bucket2.add(inv);
                        break;
                    case "VEU":
                        inv.setTargetPct(0.0);
                        bucket3.add(inv);
                        break;
                    case "IJR":
                        inv.setTargetPct(2.0);
                        bucket3.add(inv);
                        break;
                    case "VIG":  // vig position is split across bucket 2 and 3
                        Investment clone = new Investment(inv);
                        inv.sell(45.0);
                        clone.sell(55.0);
                        inv.setTargetPct(12.5);
                        bucket2.add(inv);
                        clone.setTargetPct(10.0);
                        bucket3.add(clone);
                        break;
                    case "VTIP":
                        inv.setTargetPct(0.0);
                        bucket2.add(inv);
                        break;
                    case "FACFC":
                        annuity.add(inv);
                        break;
                    case "FNMIX":
                        inv.setTargetPct(2.5);
                        bucket3.add(inv);
                        break;
                    case "FAGIX":
                        inv.setTargetPct(2.5);
                        bucket3.add(inv);
                        break;
                    case "FFHRX":
                        inv.setTargetPct(7.5);
                        bucket2.add(inv);
                        break;
                    default:
                        System.out.println("unknown investment!" + inv.toString());

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

    public void printBuckets() {
        Double sizeWOAnnuity = portfolioSize - annuity.getBucketSize();

        System.out.println ("Portfolio Size: " + String.format("%.2f", portfolioSize));
        System.out.println ("Cost Basis: " + String.format("%.2f", portfolioBasis));
        System.out.println ("Unrealized Profits: " + String.format("%.2f", (portfolioSize -portfolioBasis)));
        System.out.println ("+");
        System.out.println (" Bucket1: " + String.format("%.2f", bucket1.getBucketSize()) + " Portfolio %: " + String.format("%.2f", 100 * bucket1.getBucketSize()/sizeWOAnnuity) + " Target: 10%"+ " Out of balance by: "+ String.format("%.2f", (.1 * sizeWOAnnuity) - bucket1.getBucketSize()) );
        bucket1.printBucket(sizeWOAnnuity);
        System.out.println ("+");
        System.out.println (" Bucket2: " + String.format("%.2f", bucket2.getBucketSize()) + " Portfolio %: " + String.format("%.2f", 100 * bucket2.getBucketSize()/sizeWOAnnuity) + " Target: 55%");
        bucket2.printBucket(sizeWOAnnuity);
        System.out.println ("+");
        System.out.println (" Bucket3: " + String.format("%.2f", bucket3.getBucketSize()) + " Portfolio %: " + String.format("%.2f", 100 * bucket3.getBucketSize()/sizeWOAnnuity) + " Target: 35%");
        bucket3.printBucket(sizeWOAnnuity);
        System.out.println ("+");
        System.out.println (" Annuity: " + String.format("%.2f", annuity.getBucketSize()));
        System.out.println ("+");
        Double totalBucketSize = + bucket1.getBucketSize()+ bucket2.getBucketSize() + bucket3.getBucketSize() + annuity.getBucketSize();

        System.out.println ("sum of bucket sizes: " + String.format("%.2f", totalBucketSize ));

    }

    public Investment get(String symbol) {
        for (Investment inv: investments )
            if (inv.getSymbol().equalsIgnoreCase(symbol)) {

                return inv;
            }

        return null;
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
