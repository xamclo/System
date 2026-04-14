package com.queueingsystem.model;

import java.io.Serializable;

//  Model for Student data
// Represents basic info ng student sa system
public class Student implements Serializable {

    //  Student name
    private final String name;

    // 👉 Student ID (unique identifier)
    private final String studentId;

    // 👉 Constructor to create student object
    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    // 👉 Get student name
    public String getName() {
        return name;
    }

    // 👉 Get student ID
    public String getStudentId() {
        return studentId;
    }
}