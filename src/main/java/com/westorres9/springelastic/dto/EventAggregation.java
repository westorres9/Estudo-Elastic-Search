package com.westorres9.springelastic.dto;

import java.util.ArrayList;
import java.util.List;

public class EventAggregation {
    private String userName;
    private List<SumPerCategory> sumPerCategory;

    public EventAggregation() {
    }

    public EventAggregation(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<SumPerCategory> getSumPerCategory() {
        return sumPerCategory;
    }

    public void setSumPerCategory(List<SumPerCategory> sumPerCategory) {
        this.sumPerCategory = sumPerCategory;
    }
}
