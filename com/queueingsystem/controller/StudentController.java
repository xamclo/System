package com.queueingsystem.controller;

import com.queueingsystem.enumtypes.OfficeType;
import com.queueingsystem.model.QueueTicket;
import com.queueingsystem.service.QueueService;

// This controller is for STUDENTS
// Siya yung ginagamit para gumawa ng ticket (pila)
public class StudentController {

    //  Connection to service (dito ginagawa yung main logic)
    private final QueueService queueService;

    //  Constructor (inject service)
    public StudentController(QueueService queueService) {
        this.queueService = queueService;
    }

    //  Create ticket for student
    // Input: name, studentId, officeType
    // Output: QueueTicket
    public QueueTicket createTicket(String name, String studentId, OfficeType officeType) {

        //  Pass lang sa service (no logic here)
        return queueService.createTicket(name, studentId, officeType);
    }
}