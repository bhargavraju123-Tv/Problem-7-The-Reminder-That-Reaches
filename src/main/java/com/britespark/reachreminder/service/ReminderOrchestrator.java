package com.britespark.reachreminder.service;

import com.britespark.reachreminder.data.AppointmentRepository;
import com.britespark.reachreminder.data.ContactRepository;
import com.britespark.reachreminder.domain.Appointment;
import com.britespark.reachreminder.domain.Contact;
import com.britespark.reachreminder.policy.PolicyDecision;
import com.britespark.reachreminder.policy.ReminderPolicyEngine;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReminderOrchestrator {

    private final AppointmentRepository appointments;
    private final ContactRepository contacts;
    private final ReminderPolicyEngine policyEngine;
    private final FallbackEngine fallbackEngine;
    private final ReportingService reporting;

    private final Set<String> processedAppointments = new HashSet<>();

    // NEW: Track how many times we contact each resident to enforce the 7-day limit
    private final Map<String, Integer> residentContactCounts = new HashMap<>();

    public ReminderOrchestrator(
            AppointmentRepository appointments,
            ContactRepository contacts,
            ReminderPolicyEngine policyEngine,
            FallbackEngine fallbackEngine,
            ReportingService reporting) {
        this.appointments = appointments;
        this.contacts = contacts;
        this.policyEngine = policyEngine;
        this.fallbackEngine = fallbackEngine;
        this.reporting = reporting;
    }

    public void runReminders() {
        // PRIORITIZATION: Sort appointments by Date (Closest appointments get priority)
        List<Appointment> sortedAppointments = new ArrayList<>(appointments.findAll());
        sortedAppointments.sort(Comparator.comparing(Appointment::scheduledAt));

        for (Appointment apt : sortedAppointments) {

            if (processedAppointments.contains(apt.appointmentId())) {
                continue;
            }

            Contact contact = contacts.findById(apt.residentId());

            // DAY 2 RULE: Check Rate Limit BEFORE anything else
            // Note: If you received a historical contacts CSV for Day 2, you would load those previous counts into this map first!
            int pastContacts = residentContactCounts.getOrDefault(contact.residentId(), 0);
            if (pastContacts >= 2) {
                reporting.logSuppression(apt, "RATE_LIMIT_EXCEEDED");
                continue;
            }

            PolicyDecision decision = policyEngine.evaluate(contact);

            if (!decision.isAllowed()) {
                reporting.logSuppression(apt, decision.getReason());
                continue;
            }

            String message = generateMessage(apt, contact);
            System.out.println("Sending to " + contact.name() + ": " + message);
            // DAY 2 RULE: An attempt counts as a contact whether it fails or succeeds
            residentContactCounts.put(contact.residentId(), pastContacts + 1);

            var result = fallbackEngine.executeFallback(contact, decision.getEligibleChannels(), message);

            reporting.logAttempt(apt, result);
            processedAppointments.add(apt.appointmentId());
        }

        reporting.printSummary();
    }

    private String generateMessage(Appointment apt, Contact contact) {
        String lang = (contact.language() != null) ? contact.language().toLowerCase() : "en";

        switch (lang) {
            case "es":
                return "Recordatorio de cita: Su cita está programada para el "
                        + apt.scheduledAt().toLocalDate() + " a las " + apt.scheduledAt().toLocalTime()
                        + " en " + apt.location() + ". Servicio: " + apt.serviceType() + ".";
            case "fr":
                return "Rappel de rendez-vous : Votre rendez-vous est prévu le "
                        + apt.scheduledAt().toLocalDate() + " à " + apt.scheduledAt().toLocalTime()
                        + " à " + apt.location() + ". Service : " + apt.serviceType() + ".";
            case "en":
            default:
                return "Appointment reminder: Your appointment is scheduled for "
                        + apt.scheduledAt().toLocalDate() + " at " + apt.scheduledAt().toLocalTime()
                        + " at " + apt.location() + ". Service: " + apt.serviceType() + ".";
        }
    }
}