package com.queueingsystem.model;

import com.queueingsystem.enumtypes.OfficeType;
import com.queueingsystem.enumtypes.QueueState;

import java.io.Serializable;

// Model for Queue Ticket
// Represents isang ticket sa pila (main object sa system)
public class QueueTicket implements Serializable {

    // Ticket number (e.g. F001, R002)
    private final String number;

    //  Student na may-ari ng ticket
    private final Student student;

    //  Anong office pupuntahan
    private final OfficeType officeType;

    //  Current status ng ticket (WAITING, SERVING, etc.)
    private QueueState state;

    //  Assigned window (kung saan siya tatawagin)
    private Integer assignedWindow;

    //  Time kung kailan ginawa ticket
    private final long createdAt;

    //  Time kung kailan tinawag
    private long calledAt;

    //  Constructor (default state is WAITING)
    public QueueTicket(String number, Student student, OfficeType officeType) {
        this.number = number;
        this.student = student;
        this.officeType = officeType;
        this.state = QueueState.WAITING;                      // 👉 default status
        this.createdAt = System.currentTimeMillis();          // 👉 timestamp created
        this.calledAt = 0L;                                   // 👉 not yet called
    }

    //  Get ticket number
    public String getNumber() {
        return number;
    }

    //  Get student info
    public Student getStudent() {
        return student;
    }

    //  Get office type
    public OfficeType getOfficeType() {
        return officeType;
    }

    //  Get current state
    public QueueState getState() {
        return state;
    }

    //  Update state (WAITING → SERVING → COMPLETED, etc.)
    public void setState(QueueState state) {
        this.state = state;
    }

    //  Get assigned window
    public Integer getAssignedWindow() {
        return assignedWindow;
    }

    //  Set window number
    public void setAssignedWindow(Integer assignedWindow) {
        this.assignedWindow = assignedWindow;
    }

    // Get creation time
    public long getCreatedAt() {
        return createdAt;
    }

    //  Get time when called
    public long getCalledAt() {
        return calledAt;
    }

    // Set time when called
    public void setCalledAt(long calledAt) {
        this.calledAt = calledAt;
    }
}