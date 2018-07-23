package com.StanPerson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Buckets {
    public static BucketConfiguration bucketConfiguration = new BucketConfiguration();


    /*
    Read a CSV file from Fidelity that has current portfolio.
     */
    public static List<String> readFidelityCSV(String name) {

        /*
        csv from Fidelity has the first line set to:
           1: "Account Name/Number",
           2: "Symbol","Description",  <-------
           3: "Quantity","Last Price", <-------
           4: "Last Price Change",
           5: "Current Value",         <-------
           6: "Today's Gain/Loss Dollar",
           7: "Today's Gain/Loss Percent",
           8: "Total Gain/Loss Dollar",
           9: "Total Gain/Loss Percent",
           10: Cost Basis Per Share",
           11: "Cost Basis Total",      <--------
           12: "Type"

           There are several blank/text lines and then a Date downloaded field.


         */
        List<String> portfolio = new ArrayList<>();

        File file= new File(name);

        // this gives you a 2-dimensional array of strings
        List<String> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNextLine()){
                String line= inputStream.nextLine();
                if (line.isEmpty()) break;

                line = line.replaceAll(Pattern.quote("$"), "");
                line = line.replaceAll(Pattern.quote("n/a"), "0");
                line = line.replaceAll("--", "0");
                line = line.replaceAll("\"", "");
                lines.add(line);
            }

            // now look for the "date downloaded" field
            while ( inputStream.hasNextLine()){
                String line = inputStream.nextLine();
                if (line.isEmpty()) continue;
                if (line.startsWith(",,,")) continue; // numbers sets empty lines to have all commas...why?
                line=line.replaceAll("\"", "");
                if (line.startsWith("Date")) {
                    lines.add(0, line);
                    break;
                }
            }

            inputStream.close();
         }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return lines;
    }



    private static List<String> readPortfolioPlan(String name) {

        File file= new File(name);

        // this gives you a 2-dimensional array of strings
        List<String> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNextLine()){
                String line= inputStream.nextLine();
                if (line.isEmpty()) break;
                lines.add(line);
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return lines;

    }
    private static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> files = new ArrayList<String>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                // ignore directories for this simple case
                // listFilesForFolder(fileEntry);
            } else {
                //System.out.println(fileEntry.getName());
                files.add(fileEntry.getName());

            }
        }
        return files;
    }

    public static void main(String[] args) {

        // this is where the Portfolio_Position* and PositionPlan files are stored
        String portfolioPlanPath = "/Users/stanperson/Desktop/PortfolioPositions/";

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

        List <String> portfolioPlan = readPortfolioPlan(portfolioPlanPath + "PortfolioPlan.csv");

        // from the list of strings, build a a bucketconfiguration (InvestmentAllocations)
        for (String line : portfolioPlan) {
            bucketConfiguration.add(line);
        }

        List<String> portfolioCSV;
        System.out.println( "Reading from: " + portfolioPlanPath + positionFileName);
        portfolioCSV = readFidelityCSV(args[0]);
        int lineNo = 1;

        // transform from list of String to List of Investments
        lineNo = 0;
        Portfolio portfolio = new Portfolio();
        String dateDownloaded="none found";
        for (String line: portfolioCSV) {
            // first line has a date downloaded in it
            if (lineNo == 0) {
                dateDownloaded = line;
            }
            // second line has the column headers in it...skip it
            if (lineNo > 1) {
                Investment inv = new Investment(line);
                portfolio.add(inv);
            }
            lineNo++;
        }
        // verify that everything in the portfolio plan is also in the portfolio.
        // note that the portfolio content is driven by the file from Fidelity. The plan may have things not in currently in
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
        ;

        portfolio.bucketize(bucketConfiguration);

        System.out.println ("Buckets");
        portfolio.printBuckets(bucketConfiguration);

    }

}

/*

    public static void main(String[] args) {
        System.out.println("hello world");
        try{
            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream("/Users/stanperson/Downloads/PP.csv");
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                // Print the content on the console
                System.out.println (strLine);
            }
            //Close the input stream
            in.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
*/
/*
public class CsvParser {

    public static void main(String[] args) {
        String fileName= "read_ex.csv";
        File file= new File(fileName);

        // this gives you a 2-dimensional array of strings
        List<List<String>> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNext()){
                String line= inputStream.next();
                String[] values = line.split(",");
                // this adds the currently parsed line to the 2-dimensional string array
                lines.add(Arrays.asList(values));
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // the following code lets you iterate through the 2-dimensional array
        int lineNo = 1;
        for(List<String> line: lines) {
            int columnNo = 1;
            for (String value: line) {
                System.out.println("Line " + lineNo + " Column " + columnNo + ": " + value);
                columnNo++;
            }
            lineNo++;
        }
    }

}

public class CsvParser {

    public static void main(String[] args) {
        String fileName= "read_ex.csv";
        File file= new File(fileName);

        // this gives you a 2-dimensional array of strings
        List<List<String>> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNext()){
                String line= inputStream.next();
                String[] values = line.split(",");
                // this adds the currently parsed line to the 2-dimensional string array
                lines.add(Arrays.asList(values));
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // the following code lets you iterate through the 2-dimensional array
        int lineNo = 1;
        for(List<String> line: lines) {
            int columnNo = 1;
            for (String value: line) {
                System.out.println("Line " + lineNo + " Column " + columnNo + ": " + value);
                columnNo++;
            }
            lineNo++;
        }
    }

}

 */