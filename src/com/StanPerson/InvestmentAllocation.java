package com.StanPerson;

public class InvestmentAllocation {
    private String ticker;
    private String description;
    private Double bucket1Pct;
    private Double bucket2Pct;
    private Double bucket3Pct;
    private boolean split = false;
    private Double bucket2AllocationPct;
    private Double bucket3AllocationPct;
    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public Double getBucket2AllocationPct() {
        return bucket2AllocationPct;
    }

    public void setBucket2AllocationPct(Double bucket2AllocationPct) {
        this.bucket2AllocationPct = bucket2AllocationPct;
    }

    public Double getBucket3AllocationPct() {
        return bucket3AllocationPct;
    }

    public void setBucket3AllocationPct(Double bucket3Allocation) {
        this.bucket3AllocationPct = bucket3Allocation;
    }



    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBucket1Pct() {
        return bucket1Pct;
    }

    public void setBucket1Pct(Double bucket1Pct) {
        this.bucket1Pct = bucket1Pct;
    }

    public Double getBucket2Pct() {
        return bucket2Pct;
    }

    public void setBucket2Pct(Double bucket2Pct) {
        this.bucket2Pct = bucket2Pct;
    }

    public Double getBucket3Pct() {
        return bucket3Pct;
    }

    public void setBucket3Pct(Double bucket3Pct) {
        this.bucket3Pct = bucket3Pct;
    }


    @Override
    public String toString() {
        return  "ticker='" + ticker +
                ", description='" + description +
                ", bucket1Pct=" + bucket1Pct +
                ", bucket2Pct=" + bucket2Pct +
                ", bucket3Pct=" + bucket3Pct +
                '}';
    }
}
