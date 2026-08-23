package com.britespark.reachreminder.domain;

public record ReminderResult(
        ChannelType channel,
        String status,
        String detail,
        boolean success
) {}