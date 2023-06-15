package com.westorres9.springelastic.dto;

public class SumPerApp {

    private String app;
    private double duration;

    public SumPerApp() {
    }

    public SumPerApp(String app, double duration) {
        this.app = app;
        this.duration = duration;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}

