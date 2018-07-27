package com.StanPerson.Fidelity;

import com.StanPerson.Investment;
import com.StanPerson.Portfolio;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.StanPerson.Utility.FileUtilities.listFilesForFolder;

public class FileUtilities {
    /*
    Read a CSV file from Fidelity that has current portfolio.
     */
    public static Portfolio readFidelityCSV(String portfolioPath, Double pendingActivity) {
        Portfolio portfolio = new Portfolio();

        // retrive the name of the files the PortfolioPositions directory
        File f = new File(portfolioPath);
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


        File file= new File(portfolioPath + positionFileName);

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
            System.exit(-1);
        }

        int lineNo = 0;

        // transform from list of String (comma-delimited investments) to List of Investment objects

        String dateDownloaded="none found";

        for (String line: lines) {
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
        if( pendingActivity != 0.0) {
            Investment pendingCash = new Investment();
            pendingCash.setInvClass("Cash");
            pendingCash.setSymbol("Pending");
            pendingCash.setCurrValue(pendingActivity);
            portfolio.add(pendingCash);
        }
        portfolio.setDateDownLoaded(dateDownloaded);
        portfolio.setPendingActivity(pendingActivity);
        return portfolio;
    }

}
