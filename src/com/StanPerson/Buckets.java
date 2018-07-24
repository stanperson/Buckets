package com.StanPerson;

import static com.StanPerson.Fidelity.FileUtilities.readFidelityCSV;
import static com.StanPerson.Utility.FileUtilities.readPortfolioPlan;


public class Buckets {


    private static BucketConfiguration bucketConfiguration = new BucketConfiguration();
    public static Portfolio portfolio = new Portfolio();


    public static void main(String[] args) {

        // this is where the Portfolio_Position* and PositionPlan files are stored
        String portfolioPath = "/Users/stanperson/Desktop/PortfolioPositions/";

        // read the portfolio allocation plan. If it isn't found this routine will exit.
        bucketConfiguration = readPortfolioPlan(portfolioPath + "PortfolioPlan.csv");

        portfolio = readFidelityCSV(portfolioPath);

        portfolio.validate(bucketConfiguration);

        portfolio.print();

        // allocate the portfolio into 3 buckets
        portfolio.bucketize(bucketConfiguration);

        // print out the bucketized portfolio
        System.out.println ("Buckets");
        portfolio.printBuckets(bucketConfiguration);

    }

}

