package com.socialnet.pojos;

public class UserMessage implements Comparable<UserMessage>{
    private String authorId;
    private Message message;

    public UserMessage(String authorId, Message message) {
        this.authorId = authorId;
        this.message = message;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public int compareTo(UserMessage o) {
        return (int) (this.message.getTimestamp() - o.message.getTimestamp());
    }
}
