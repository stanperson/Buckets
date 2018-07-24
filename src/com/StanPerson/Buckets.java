package com.StanPerson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import com.StanPerson.Utility.FileUtilities;

import static com.StanPerson.Utility.FileUtilities.listFilesForFolder;
import static com.StanPerson.Utility.FileUtilities.readFidelityCSV;
import static com.StanPerson.Utility.FileUtilities.readPortfolioPlan;


public class Buckets {


    private static BucketConfiguration bucketConfiguration = new BucketConfiguration();



    public static void main(String[] args) {

        // this is where the Portfolio_Position* and PositionPlan files are stored
        String portfolioPlanPath = "/Users/stanperson/Desktop/PortfolioPositions/";

        // retrive the name of the files the PortfolioPositions directory
        File f = new File(portfolioPlanPath);
        ArrayList<String> files = listFilesForFolder(f);


        //find the Portfolio_Position-mmm-dd-yyyy.csv file that is in this directory
        String positionFileName = "not found";
        for (String name: files) {
            if (name.startsWith("Portfolio_Position")) {
                positionFileName = name;
                break;
            }
        }

        if (positionFileName.equalsIgnoreCase("not found")) {
            System.out.println("Portfolio_Position file not found in directory:/Users/stanperson/Desktop/PortfolioPositions" );
            System.exit(-1);
        }

        // read the portfolio allocation plan. If it isn't found this routine will exit.
        List <String> portfolioPlan = readPortfolioPlan(portfolioPlanPath + "PortfolioPlan.csv");

        // from the list of strings, build a a bucketconfiguration (InvestmentAllocations)
        for (String line : portfolioPlan) {
            bucketConfiguration.add(line);
        }

        List<String> portfolioCSV;
        System.out.println( "Reading from: " + portfolioPlanPath + positionFileName);
        // this simply reads the Portfolio_Position* file and puts it one line at a time into portfolioCSV.
        portfolioCSV = readFidelityCSV(args[0]);
        int lineNo = 0;

        // transform from list of String (comma-delimited investments) to List of Investment objects
        Portfolio portfolio = new Portfolio();
        String dateDownloaded="none found";

        for (String line: portfolioCSV) {
            // first line has a date downloaded in it
            if (lineNo == 0) {
                dateDownloaded = line;
            }
            // second line has the column headers in it...skip it
            if (lineNo > 1) {
                Investment inv = new Investment(line); // creates an investment object from CSV text.
                portfolio.add(inv);
            }
            lineNo++;
        }

        // verify that everything in the portfolio plan is also in the portfolio.
        // Thus far, the portfolio content is determined by the file from Fidelity (the account info). The plan may have things not in currently in
        // the Fidelity Portfolio.
        List<Investment> invs = portfolio.getInvestments();
        ArrayList<InvestmentAllocation> alls = bucketConfiguration.getAllocations();
        for (InvestmentAllocation allocation: alls) {
            String allocationTicker = allocation.getTicker();
            Investment i =portfolio.get(allocationTicker);
            if (portfolio.get( allocationTicker) == null ){
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
                    if ( bucketPct == 0.) {
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
        System.out.println( "Portfolio:" + dateDownloaded);
        portfolio.print();

        // allocate the portfolio into 3 buckets
        portfolio.bucketize(bucketConfiguration);

        // print out the bucketized portfolio
        System.out.println ("Buckets");
        portfolio.printBuckets(bucketConfiguration);

    }

}

