package com.queueingsystem.controller;

import com.queueingsystem.enumtypes.OfficeType;
import com.queueingsystem.model.QueueTicket;
import com.queueingsystem.service.QueueService;

// This controller is for ADMIN / STAFF
// Siya yung nagco-control ng pila (serve, skip, done)
public class AdminController {

    //  Connection to service
    private final QueueService queueService;

    public AdminController(QueueService queueService) {
        this.queueService = queueService;
    }

    //  Get next student sa pila
    public QueueTicket serveNext(OfficeType officeType, int windowNumber) {
        return queueService.serveNext(officeType, windowNumber);
    }

    //  Call specific ticket (manual pick)
    public QueueTicket serveSelected(OfficeType officeType, int windowNumber, String ticketNumber) {
        return queueService.serveSelected(officeType, windowNumber, ticketNumber);
    }

    // 👉 Mark as done (tapos na transaction)
    public void done(OfficeType officeType, int windowNumber) {
        queueService.markDone(officeType, windowNumber);
    }

    // 👉 Skip current (kung wala yung student)
    public void skip(OfficeType officeType, int windowNumber) {
        queueService.skipCurrent(officeType, windowNumber);
    }

    // 👉 Get current na sineserve sa window
    public QueueTicket current(OfficeType officeType, int windowNumber) {
        return queueService.getCurrentServing(officeType, windowNumber);
    }
}