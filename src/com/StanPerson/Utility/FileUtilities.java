package com.StanPerson.Utility;

import com.StanPerson.BucketConfiguration;

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


    public static BucketConfiguration readPortfolioPlan(String name) {
        BucketConfiguration bucketConfiguration = new BucketConfiguration();

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

        // from the list of strings, build a a bucketconfiguration (InvestmentAllocations)
        for (String line : lines) {
            bucketConfiguration.add(line);
        }

        return bucketConfiguration;

    }

}
