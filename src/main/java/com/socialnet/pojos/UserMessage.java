package com.socialnet.pojos;

public class UserMessage implements Comparable<UserMessage>{
    private String authorId;
    private Message message;

    public UserMessage() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserMessage)) return false;

        UserMessage that = (UserMessage) o;

        if (authorId != null ? !authorId.equals(that.authorId) : that.authorId != null) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = authorId != null ? authorId.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
