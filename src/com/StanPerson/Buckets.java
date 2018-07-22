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
                System.out.println( line);
                lines.add(line);
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return lines;

    }

    public static void main(String[] args) {

        String portfolioPlanPath = "/Users/stanperson/Downloads/PortfolioPlan.csv";
        List <String> portfolioPlan = readPortfolioPlan(portfolioPlanPath);

        for (String line : portfolioPlan) {
            System.out.println(line);
            bucketConfiguration.add(line);
        }
        bucketConfiguration.print();

        List<String> portfolioCSV;
        System.out.println( "Reading from: " + args[0]);
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
        System.out.println( "Portfolio:" + dateDownloaded);
        portfolio.print();
        ;

        portfolio.bucketize();

        System.out.println ("Buckets");
        portfolio.printBuckets();

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