package com.queueingsystem.service;

import com.queueingsystem.enumtypes.OfficeType;

import java.util.EnumMap;
import java.util.Map;

// 📌 Responsible for generating queue numbers (e.g. F-001, R-002)
// 📌 Separate counter per office (Finance, Registrar)
public class QueueNumberGenerator {

    // 📌 Stores current count per office
    private final Map<OfficeType, Integer> counters = new EnumMap<>(OfficeType.class);

    public QueueNumberGenerator() {

        // 📌 Initialize counters (start at 1 per office)
        for (OfficeType officeType : OfficeType.values()) {
            counters.put(officeType, 1);
        }
    }

    // 📌 Thread-safe method (para walang duplicate number)
    public synchronized String nextNumber(OfficeType officeType) {

        // 📌 Get current number
        int value = counters.get(officeType);

        // 📌 Increment for next use
        counters.put(officeType, value + 1);

        // 📌 Format: PREFIX-001 (e.g. F-001)
        return String.format("%s-%03d", officeType.getPrefix(), value);
    }
}