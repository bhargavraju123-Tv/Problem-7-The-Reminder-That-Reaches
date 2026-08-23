package com.britespark.reachreminder.service;

import com.britespark.reachreminder.domain.Appointment;
import com.britespark.reachreminder.domain.ReminderResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportingService {

    private int totalAttempted = 0;
    private int humanReach = 0;
    private int transportDelivery = 0;
    private final Map<String, Integer> suppressions = new HashMap<>();

    public void logSuppression(Appointment apt, String reason) {
        suppressions.merge(reason, 1, Integer::sum);
    }

    public void logAttempt(Appointment apt, ReminderResult result) {
        totalAttempted++;

        if (result.success()) {
            if ("human".equals(result.detail())) {
                humanReach++;
            }
            // If it reached a human OR was delivered/voicemail left, count as transport delivery
            transportDelivery++;
        }

        // In a real system, you might save this to a database or file
        System.out.printf("Attempted %s for Resident %s via %s -> Status: %s, Detail: %s%n",
                apt.appointmentId(), apt.residentId(), result.channel(), result.status(), result.detail());
    }

    public void printSummary() {
        System.out.println("\n========================================");
        System.out.println("REMINDER RUN REPORT");
        System.out.println("========================================");
        System.out.println("Attempted:              " + totalAttempted);
        System.out.println("Confirmed human reach:  " + humanReach);
        System.out.println("Transport delivery:     " + transportDelivery);

        System.out.println("\n--- Suppressions ---");
        suppressions.forEach((reason, count) ->
                System.out.println(reason + ": " + count)
        );
        System.out.println("========================================\n");
    }
}