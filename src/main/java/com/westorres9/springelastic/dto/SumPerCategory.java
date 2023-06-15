package com.westorres9.springelastic.dto;

public class SumPerCategory {

    private String category;
    private double duration;

    public SumPerCategory() {
    }

    public SumPerCategory(String category, double duration) {
        this.category = category;
        this.duration = duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
