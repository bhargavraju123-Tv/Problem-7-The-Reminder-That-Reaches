package com.britespark.reachreminder.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Contact(
        String residentId,
        String name,
        String mobile,
        String landline,
        String email,
        String language,
        boolean smsOptOut,
        boolean voiceOptOut,
        boolean emailOptOut,
        LocalDate numberLastVerified
) {}
