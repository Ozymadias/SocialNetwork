package com.socialnet.pojos;

public class Message {
    private final long timestamp;
    private final String content;

    public Message() {
        timestamp = 0;
        content = null;
    }

    public Message(long timestamp, String content) {
        this.timestamp = timestamp;
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }
}
