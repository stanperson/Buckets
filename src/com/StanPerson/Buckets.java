package com.StanPerson;

import java.util.Scanner;

import static com.StanPerson.Fidelity.FileUtilities.readFidelityCSV;
import static com.StanPerson.Utility.FileUtilities.readPortfolioPlan;


public class Buckets {


    public static BucketConfiguration bucketConfiguration = new BucketConfiguration();
    public static Portfolio portfolio = new Portfolio();

    // arg[0]: path to data folder (default /Users/stanperson/Desktop/PortfolioPositions/)
    // arg[1]: file name for Portfolio Plan (default: PortfolioPlan.csv)
    // arg[2]: Portfolio_POsition file. (Default: Any file in PortfolioPositions directory that starts with Portfolio_Position first one).

    public static void main(String[] args) {
        String portfolioPath = "/Users/stanperson/Desktop/PortfolioPositions/";   // directory of portfolio position files as a portfolio plan file
        String portfolioPlan = "PortfolioPlan.csv";
        Double pendingActivity = 0.0;                // pass in pending activity that doesn't show up in Fidelity Portfolio_posion* files.
        Scanner in = new Scanner (System.in);
        if (args.length > 0) {
            System.out.println("args length "+  args.length);
            for (int ii =0; ii<args.length; ii+=2) {
                System.out.println(ii +":"+ args[ii]);
                switch ( args[ii].toLowerCase()){
                    case "-path":
                        portfolioPath= args[ii+1];
                        System.out.println("Setting path: " + portfolioPath + " enter over-ride value: ");
                        String overRide = in.nextLine();
                        if (!overRide.isEmpty()) {
                            portfolioPath = overRide;
                            System.out.println("Path set to:" + portfolioPath);
                        }

                        break;
                    case "-plan":
                        portfolioPlan = args[ii+1];
                        System.out.println("setting plan: " + portfolioPlan+ " enter over-ride value: ");
                        overRide = in.nextLine();
                        if (!overRide.isEmpty()) {
                            portfolioPlan = overRide;
                            System.out.println("Plan set to:" + portfolioPlan);
                        }

                        break;
                    case "-pending":
                        pendingActivity = Double.parseDouble(args[ii+1]);
                        System.out.println("Pending activity: " + pendingActivity+ " enter over-ride value: ");
                        overRide = in.nextLine();
                        if (!overRide.isEmpty()) {
                            pendingActivity = Double.parseDouble(overRide);
                            System.out.println("Pending actity set to:" + pendingActivity);
                        }

                        break;
                    default:
                        System.out.println("illegal arguments");
                        System.exit(-1);
                }
                if (args[ii].equalsIgnoreCase("-path")) {

                }
            }
        }
        else
            System.out.println("no args");



        // read the portfolio allocation plan. If it isn't found this routine will exit.
        bucketConfiguration = readPortfolioPlan(portfolioPath + portfolioPlan);

        portfolio = readFidelityCSV(portfolioPath, pendingActivity);

        portfolio.validate(bucketConfiguration);

        portfolio.print();

        // allocate the portfolio into 3 buckets
        portfolio.bucketize(bucketConfiguration);

        // print out the bucketized portfolio
        System.out.println ("Buckets");
        portfolio.printBuckets(bucketConfiguration);

    }

}

