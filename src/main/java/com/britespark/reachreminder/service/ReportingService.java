package com.britespark.reachreminder.service;

import com.britespark.reachreminder.domain.Appointment;
import com.britespark.reachreminder.domain.ReminderResult;
import org.springframework.stereotype.Service;

@Service
public class ReportingService {

    public void logSuppression(Appointment apt, String reason) {
        // Stub
    }

    public void logAttempt(Appointment apt, ReminderResult result) {
        // Stub
    }

    public void printSummary() {
        // Stub
    }
}