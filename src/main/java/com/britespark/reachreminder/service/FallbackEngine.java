package com.britespark.reachreminder.service;

import com.britespark.reachreminder.channel.ChannelGateway;
import com.britespark.reachreminder.channel.ChannelResult;
import com.britespark.reachreminder.domain.ChannelType;
import com.britespark.reachreminder.domain.Contact;
import com.britespark.reachreminder.domain.ReminderResult;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FallbackEngine {

    private final ChannelGateway gateway;

    public FallbackEngine(ChannelGateway gateway) {
        this.gateway = gateway;
    }

    public ReminderResult executeFallback(Contact contact, List<ChannelType> channels, String message) {
        int attempt = 1;
        for (ChannelType channel : channels) {
            String endpoint = resolveEndpoint(contact, channel);
            ChannelResult result = gateway.send(channel.name().toLowerCase(), endpoint, message, attempt);

            // Stopping Rule Evaluator
            if (isHumanReach(result) || isConfirmedDelivery(result)) {
                return new ReminderResult(channel, result.status(), result.detail(), true);
            }
            attempt++;
        }
        return new ReminderResult(null, "exhausted", "No channels succeeded", false);
    }

    private boolean isHumanReach(ChannelResult res) {
        return "answered".equals(res.status()) && "human".equals(res.detail());
    }

    private boolean isConfirmedDelivery(ChannelResult res) {
        return "delivered".equals(res.status()) || ("answered".equals(res.status()) && "voicemail_left".equals(res.detail()));
    }

    private String resolveEndpoint(Contact c, ChannelType type) {
        return switch (type) {
            case SMS, VOICE_MOBILE -> c.mobile();
            case VOICE_LANDLINE -> c.landline();
            case EMAIL -> c.email();
        };
    }
}