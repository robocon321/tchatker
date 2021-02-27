package com.example.tchatker.model;

public class Message {
    private String sender;
    private String content;
    private String status;
    private String type;
    private Time time;

    public Message(String sender, String content, String status, String type, Time time) {
        this.sender = sender;
        this.content = content;
        this.status = status;
        this.type = type;
        this.time = time;
    }

    public Message(String sender, String content, String status, String type) {
        this.sender = sender;
        this.content = content;
        this.status = status;
        this.type = type;
        this.time = new Time();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", time=" + time +
                '}';
    }
}
