package com.StanPerson.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FileUtilities {
    public static ArrayList<String> listFilesForFolder(final File folder) {
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



    public static List<String> readPortfolioPlan(String name) {

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

}
