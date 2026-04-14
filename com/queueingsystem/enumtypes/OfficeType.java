package com.queueingsystem.enumtypes;

public enum OfficeType {
    FINANCE("F"),
    REGISTRAR("R");

    private final String prefix;

    OfficeType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}