package com.britespark.reachreminder.policy;

import com.britespark.reachreminder.domain.ChannelType;
import java.util.Collections;
import java.util.List;

public class PolicyDecision {
    private final boolean allowed;
    private final String reason;
    private final List<ChannelType> eligibleChannels;

    private PolicyDecision(boolean allowed, String reason, List<ChannelType> eligibleChannels) {
        this.allowed = allowed;
        this.reason = reason;
        this.eligibleChannels = eligibleChannels;
    }

    public static PolicyDecision blocked(String reason) {
        return new PolicyDecision(false, reason, Collections.emptyList());
    }

    public static PolicyDecision allowed(List<ChannelType> channels) {
        return new PolicyDecision(true, "ALLOWED", channels);
    }

    public boolean isAllowed() { return allowed; }
    public String getReason() { return reason; }
    public List<ChannelType> getEligibleChannels() { return eligibleChannels; }
}