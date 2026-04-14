package com.queueingsystem.model;

import com.queueingsystem.enumtypes.OfficeType;

// 👉 Model for Admin Account
// Represents staff/admin na gumagamit ng system
public class AdminAccount {

    //  Username for login
    private final String username;

    //  Password for login (note: ideally hashed in real system)
    private final String password;

    //  Assigned office (Finance / Registrar)
    private final OfficeType officeType;

    //  Assigned window number (e.g. Window 1, 2, etc.)
    private final int windowNumber;

    //  Constructor to create admin account
    public AdminAccount(String username, String password, OfficeType officeType, int windowNumber) {
        this.username = username;
        this.password = password;
        this.officeType = officeType;
        this.windowNumber = windowNumber;
    }

    //  Get username
    public String getUsername() {
        return username;
    }

    //  Get password
    public String getPassword() {
        return password;
    }

    //  Get assigned office
    public OfficeType getOfficeType() {
        return officeType;
    }

    //  Get window number
    public int getWindowNumber() {
        return windowNumber;
    }
}