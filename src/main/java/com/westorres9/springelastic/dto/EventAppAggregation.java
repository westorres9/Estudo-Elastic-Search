package com.westorres9.springelastic.dto;

import java.util.List;

public class EventAppAggregation {

    private String category;
    private List<SumPerApp> sumPerApp;

    public EventAppAggregation() {
    }

    public EventAppAggregation(String category, List<SumPerApp> sumPerApp) {
        this.category = category;
        this.sumPerApp = sumPerApp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<SumPerApp> getSumPerApp() {
        return sumPerApp;
    }

    public void setSumPerApp(List<SumPerApp> sumPerApp) {
        this.sumPerApp = sumPerApp;
    }
}
