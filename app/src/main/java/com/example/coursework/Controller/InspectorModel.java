package com.example.coursework.Controller;

public class InspectorModel {

    public String lastName;
    public String firstName;
    public String patronymic;
    public String department;

    public InspectorModel(String lastName, String firstName,
                          String patronymic, String department) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.department = department;
    }

    public String getFullName() {
        return lastName + " " + firstName + " " + patronymic;
    }
}

