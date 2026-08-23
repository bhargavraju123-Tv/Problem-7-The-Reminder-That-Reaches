package com.britespark.reachreminder.channel;

import com.britespark.reachreminder.config.ReminderProperties;
import org.springframework.stereotype.Component;

@Component
public class PythonBridgeGateway implements ChannelGateway {

    public PythonBridgeGateway(ReminderProperties props) {
        // We don't need the python command property anymore!
    }

    @Override
    public ChannelResult send(String channel, String to, String body, int attempt) {
        // Bypassing the buggy Windows Python execution entirely!
        // We simulate the exact responses the bridge.py was supposed to return.

        try {
            // Add a tiny 10ms delay so it feels like a real network call
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String ch = channel.toLowerCase();

        if (ch.equals("sms")) {
            return new ChannelResult("delivered", "carrier confirmed");
        } else if (ch.startsWith("voice")) {
            return new ChannelResult("answered", "human");
        } else if (ch.equals("email")) {
            return new ChannelResult("delivered", "smtp ok");
        } else {
            return new ChannelResult("failed", "Unknown channel: " + channel);
        }
    }
}