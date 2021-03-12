package com.example.tchatker.model;

public class Comment {
    private String uname;
    private String content;
    private Time time;

    public Comment(String uname, String content, Time time) {
        this.uname = uname;
        this.content = content;
        this.time = time;
    }

    public Comment() {
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "uname='" + uname + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                '}';
    }
}
