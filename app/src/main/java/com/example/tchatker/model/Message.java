package com.example.tchatker.model;

public class Message implements Comparable<Message>{
    private String sender;
    private String content;
    private boolean status;
    private String typeContent;
    private Time time;

    public Message(){

    }

    public Message(String sender, String content, boolean status, String typeContent, Time time) {
        this.sender = sender;
        this.content = content;
        this.status = status;
        this.typeContent = typeContent;
        this.time = time;
    }

    public Message(String sender, String content, boolean status, String typeContent) {
        this.sender = sender;
        this.content = content;
        this.status = status;
        this.typeContent = typeContent;
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

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTypeContent() {
        return typeContent;
    }

    public void setTypeContent(String typeContent) {
        this.typeContent = typeContent;
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
                ", type='" + typeContent + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public int compareTo(Message o) {
        if(time.getYear() > o.getTime().getYear())
            return 1;
        else if(time.getYear() < o.getTime().getYear())
            return -1;
        else{
            if(time.getMonth() > o.getTime().getMonth())
                return 1;
            else if(time.getMonth() < o.getTime().getMonth())
                return -1;
            else {
                if(time.getDay() > o.getTime().getDay())
                    return 1;
                else if(time.getDay() < o.getTime().getDay())
                    return -1;
                else{
                    if(time.getHour() > o.getTime().getHour())
                        return 1;
                    else if(time.getHour() < o.getTime().getHour())
                        return -1;
                    else{
                        if(time.getMinute() > o.getTime().getMinute())
                            return 1;
                        else if(time.getMinute() < o.getTime().getMinute())
                            return -1;
                        else{
                            if(time.getSecond() > o.getTime().getSecond())
                                return 1;
                            else
                                return -1;
                        }
                    }
                }
            }
        }
    }
}
