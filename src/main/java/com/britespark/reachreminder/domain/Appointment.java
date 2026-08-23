package com.britespark.reachreminder.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;



public record Appointment(
        String appointmentId,
        String residentId,
        LocalDateTime scheduledAt,
        String location,
        String serviceType,
        String status
) {}