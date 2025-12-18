package com.example.coursework.Controller;

public class ReportRecord {

    public String date;
    public String citizenFio;
    public String address;
    public String violation;
    public String passport;

    public ReportRecord(
            String date,
            String citizenFio,
            String address,
            String violation,
            String passport
    ) {
        this.date = date;
        this.citizenFio = citizenFio;
        this.address = address;
        this.violation = violation;
        this.passport = passport;
    }
}

