package com.westorres9.springelastic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class EventDTO {

    private String userName;
    private Long duration;
    private String app;
    private String category;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

    public EventDTO() {
    }

    public EventDTO(String userName, Long duration, String app, String category, OffsetDateTime startDate, OffsetDateTime endDate) {
        this.userName = userName;
        this.duration = duration;
        this.app = app;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }
}

