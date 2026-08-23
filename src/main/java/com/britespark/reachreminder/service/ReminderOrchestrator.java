package com.britespark.reachreminder.service;

import com.britespark.reachreminder.data.AppointmentRepository;
import com.britespark.reachreminder.data.ContactRepository;
import com.britespark.reachreminder.domain.Appointment;
import com.britespark.reachreminder.domain.Contact;
import com.britespark.reachreminder.policy.PolicyDecision;
import com.britespark.reachreminder.policy.ReminderPolicyEngine;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ReminderOrchestrator {

    private final AppointmentRepository appointments;
    private final ContactRepository contacts;
    private final ReminderPolicyEngine policyEngine;
    private final FallbackEngine fallbackEngine;
    private final ReportingService reporting;

    private final Set<String> processedAppointments = new HashSet<>();

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
        for (Appointment apt : appointments.findAll()) {

            if (processedAppointments.contains(apt.appointmentId())) {
                continue;
            }

            Contact contact = contacts.findById(apt.residentId());

            PolicyDecision decision = policyEngine.evaluate(contact);

            if (!decision.isAllowed()) {
                reporting.logSuppression(apt, decision.getReason());
                continue;
            }

            String message = generateMessage(apt, contact);

            var result = fallbackEngine.executeFallback(contact, decision.getEligibleChannels(), message);

            reporting.logAttempt(apt, result);
            processedAppointments.add(apt.appointmentId());
        }

        reporting.printSummary();
    }

    private String generateMessage(Appointment apt, Contact contact) {
        return "Appointment reminder: Your appointment is scheduled for "
                + apt.scheduledAt() + " at " + apt.location()
                + ". Service: " + apt.serviceType() + ".";
    }
}