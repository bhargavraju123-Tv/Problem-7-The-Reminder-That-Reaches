package com.britespark.reachreminder.policy;

import com.britespark.reachreminder.config.ReminderProperties;
import com.britespark.reachreminder.domain.ChannelType;
import com.britespark.reachreminder.domain.Contact;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ReminderPolicyEngine {

    private final ReminderProperties properties;
    private final Set<String> sharedContactPoints = new HashSet<>();

    public ReminderPolicyEngine(ReminderProperties properties) {
        this.properties = properties;
    }

    // Method for the CSV loader to populate the shared points
    public void addSharedContactPoint(String endpoint) {
        if (endpoint != null && !endpoint.isBlank()) {
            this.sharedContactPoints.add(endpoint);
        }
    }

    public PolicyDecision evaluate(Contact contact) {
        if (contact == null) return PolicyDecision.blocked("NO_CONTACT_RECORD");

        LocalTime now = LocalTime.now();
        LocalTime quietStart = properties.getQuietHoursStart(); // 23:00
        LocalTime quietEnd = properties.getQuietHoursEnd();     // 03:00

        // Handles overnight quiet hours: Blocks messages after 11 PM OR before 3 AM
        if (now.isAfter(quietStart) || now.isBefore(quietEnd)) {
            return PolicyDecision.blocked("QUIET_HOURS");
        }

        List<ChannelType> eligibleChannels = new ArrayList<>();

        if (!contact.smsOptOut() && hasSafePoint(contact.mobile())) eligibleChannels.add(ChannelType.SMS);

        if (!contact.voiceOptOut()) {
            if (hasSafePoint(contact.mobile())) eligibleChannels.add(ChannelType.VOICE_MOBILE);
            else if (hasSafePoint(contact.landline())) eligibleChannels.add(ChannelType.VOICE_LANDLINE);
        }

        if (!contact.emailOptOut() && hasSafePoint(contact.email())) eligibleChannels.add(ChannelType.EMAIL);

        if (eligibleChannels.isEmpty()) {
            return PolicyDecision.blocked("ALL_CHANNELS_OPTED_OUT_OR_UNSAFE");
        }

        return PolicyDecision.allowed(eligibleChannels);
    }

    private boolean hasSafePoint(String endpoint) {
        return endpoint != null && !endpoint.isBlank() && !sharedContactPoints.contains(endpoint);
    }
}