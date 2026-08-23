package com.britespark.reachreminder.channel;

public interface ChannelGateway {
    ChannelResult send(String channel, String to, String body, int attempt);
}