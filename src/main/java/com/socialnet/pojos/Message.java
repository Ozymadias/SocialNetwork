package com.socialnet.pojos;

public class Message {
    private long timestamp;
    private String content;

    public Message() {
    }

    public Message(long timestamp, String content) {
        this.timestamp = timestamp;
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
