package com.westorres9.springelastic.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class DateAggregationResult {

    private String date;
    private long count;
    private double avgDuration;
    private double totalDuration;

    public  DateAggregationResult() {
    }

    public DateAggregationResult(String date, long count, double avgDuration, double totalDuration) {
        this.date = date;
        this.count = count;
        this.avgDuration = avgDuration;
        this.totalDuration = totalDuration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(double avgDuration) {
        this.avgDuration = avgDuration;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }
}


