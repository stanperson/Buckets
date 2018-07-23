package com.StanPerson;

import java.util.ArrayList;

public class BucketConfiguration {


private ArrayList<InvestmentAllocation> allocations;

public BucketConfiguration() {
    allocations = new ArrayList<>();

}
public void add(String csvItem) {
    InvestmentAllocation ia = new InvestmentAllocation();

    String[] values = csvItem.split(",") ;
    ia.setTicker(values[0]);
    ia.setDescription( values[1]);
    ia.setBucket1Pct(Double.parseDouble(values[2]));
    ia.setBucket2Pct(Double.parseDouble(values[3]));
    ia.setBucket3Pct(Double.parseDouble(values[4]));
    allocations.add(ia);
}

public void print(){
    System.out.println("Bucket Configuration: ");
    for (InvestmentAllocation ia: allocations) {
        System.out.println(ia.toString());
    }
}

}
