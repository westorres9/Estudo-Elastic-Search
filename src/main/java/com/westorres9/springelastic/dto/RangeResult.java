package com.westorres9.springelastic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

public class RangeResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String range;
    private long quantity;

    public RangeResult() {

    }

    public RangeResult(String range, long quantity) {
        this.range = range;
        this.quantity = quantity;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
