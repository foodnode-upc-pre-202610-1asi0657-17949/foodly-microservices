package com.foodly.business.domain.model;

public class DaySchedule {

    private String day;
    private Boolean open;
    private String from;
    private String to;

    public DaySchedule() {}

    public DaySchedule(String day, Boolean open, String from, String to) {
        this.day = day;
        this.open = open;
        this.from = from;
        this.to = to;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public Boolean getOpen() { return open; }
    public void setOpen(Boolean open) { this.open = open; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
}