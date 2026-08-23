package com.britespark.reachreminder.data;

import com.britespark.reachreminder.domain.Appointment;
import com.britespark.reachreminder.domain.Contact;
import com.britespark.reachreminder.policy.ReminderPolicyEngine;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CsvLoader {

    @Value("${app.data.appointments:appointments.csv}")
    private String appointmentsFilePath;

    @Value("${app.data.contacts:contacts.csv}")
    private String contactsFilePath;

    private final AppointmentRepository appointmentRepository;
    private final ContactRepository contactRepository;
    private final ReminderPolicyEngine policyEngine;

    private static final DateTimeFormatter APPT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CsvLoader(AppointmentRepository appointmentRepository,
                     ContactRepository contactRepository,
                     ReminderPolicyEngine policyEngine) {
        this.appointmentRepository = appointmentRepository;
        this.contactRepository = contactRepository;
        this.policyEngine = policyEngine;
    }

    @PostConstruct
    public void loadData() {
        loadContacts();
        loadAppointments();
        identifySharedContactPoints();
        System.out.println("Data loading complete. Loaded " + appointmentRepository.count() + " appointments.");
    }

    private void loadContacts() {
        try (BufferedReader br = new BufferedReader(new FileReader(contactsFilePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] cols = line.split(",", -1); // -1 keeps empty trailing strings
                if (cols.length < 10) continue;

                Contact contact = new Contact(
                        cols[0].trim(), // resident_id
                        cols[1].trim(), // name
                        cols[2].trim(), // mobile
                        cols[3].trim(), // landline
                        cols[4].trim(), // email
                        cols[5].trim(), // language
                        "Y".equalsIgnoreCase(cols[6].trim()), // sms_optout
                        "Y".equalsIgnoreCase(cols[7].trim()), // voice_optout
                        "Y".equalsIgnoreCase(cols[8].trim()), // email_optout
                        parseDate(cols[9].trim()) // number_last_verified
                );
                contactRepository.save(contact);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load contacts.csv - " + e.getMessage());
        }
    }

    private void loadAppointments() {
        try (BufferedReader br = new BufferedReader(new FileReader(appointmentsFilePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                String[] cols = line.split(",", -1);
                if (cols.length < 6) continue;

                Appointment appointment = new Appointment(
                        cols[0].trim(), // appointment_id
                        cols[1].trim(), // resident_id
                        parseDateTime(cols[2].trim()), // scheduled_at
                        cols[3].trim(), // location
                        cols[4].trim(), // service_type
                        cols[5].trim()  // status
                );
                appointmentRepository.save(appointment);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load appointments.csv - " + e.getMessage());
        }
    }

    private void identifySharedContactPoints() {
        Map<String, Integer> pointFrequencies = new HashMap<>();

        for (Contact c : contactRepository.findAll()) {
            if (!c.mobile().isBlank()) pointFrequencies.merge(c.mobile(), 1, Integer::sum);
            if (!c.landline().isBlank()) pointFrequencies.merge(c.landline(), 1, Integer::sum);
            if (!c.email().isBlank()) pointFrequencies.merge(c.email(), 1, Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : pointFrequencies.entrySet()) {
            if (entry.getValue() > 1) {
                policyEngine.addSharedContactPoint(entry.getKey());
            }
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try { return LocalDate.parse(dateStr); }
        catch (DateTimeParseException e) { return null; }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) return null;
        try { return LocalDateTime.parse(dateTimeStr, APPT_FORMATTER); }
        catch (DateTimeParseException e) { return null; }
    }
}