package com.StanPerson;

import java.util.ArrayList;
import java.util.List;

public class Investment {
    private String symbol ="default";
    private String description="default";
    private Double quantity = 0.;
    private Double price = 0.;
    private Double currValue = 0.;
    private Double costBasis = 0.;
    private Double targetPct = 0.;
    private String type = "default";

    /*
        csv from Fidelity has the first line set to:
           1: "Account Name/Number",
           2: "Symbol",       <-------
           3: "Description",  <-------
           4: "Quantity",     <-------
           5: "Last Price",   <-------
           6: "Last Price Change",
           7: "Current Value",         <-------
           8: "Today's Gain/Loss Dollar",
           9: "Today's Gain/Loss Percent",
           10: "Total Gain/Loss Dollar",
           11: "Total Gain/Loss Percent",
           12: Cost Basis Per Share",
           13: "Cost Basis Total",      <--------
           14: "Type"

         */

    public Investment(){

    }

    public Investment(Investment source) {
        this.symbol = source.getSymbol();
        this.description = source.getDescription();
        this.quantity = source.quantity;
        this.price = source.getPrice();
        this.currValue = source.getCurrValue();
        this.costBasis = source.getCostBasis();
        this.targetPct = source.getTargetPct();
    }

    public Investment merge( Investment inv) {
        // add the quantities, value, cost and target pct...that's a merge.
        this.quantity += inv.quantity;
        this.currValue += inv.getCurrValue();
        this.costBasis += inv.getCostBasis();
        this.targetPct += inv.getTargetPct();
        return this;
    }

    public Investment(String line) {

        String[] values = line.split(",") ;
        symbol = values[1];
        description = values[2];
        quantity = Double.parseDouble(values[3]);
        price = Double.parseDouble(values[4]);
        currValue = Double.parseDouble(values[6]);
        costBasis = Double.parseDouble(values[12]);
        if (costBasis == 0) // fudge...n/a was replaced by 0 in input file.
            costBasis = currValue;
        targetPct = 0.0;
    }

    public void sell(Double fraction) {

        this.quantity= (fraction * this.quantity);
        this.currValue = (fraction* this.currValue);
        this.costBasis = (fraction * this.costBasis);
    }

    public Double getTargetPct() {
        return targetPct;
    }

    public void setTargetPct(Double targetPct) {
        this.targetPct = targetPct;
    }

    public String toString(){
        return " " + symbol + "|" + description +"|" + ""+quantity + "|" + ""+price + "|" + ""+ currValue + "|" + ""+ costBasis;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol){
        this.symbol = symbol;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double q) {
        quantity = q;
    }

    public Double getCurrValue () {
        return currValue;
    }

    public void setCurrValue(Double v) {
        currValue = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d ) {
        description = d;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice (Double p){
        price = p;
    }

    public Double getCostBasis() {
        return costBasis;
    }

    public void setCostBasis(Double c) {
        costBasis = c;
    }

}
