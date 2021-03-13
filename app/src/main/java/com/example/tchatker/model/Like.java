package com.example.tchatker.model;

import java.io.Serializable;

public class Like implements Serializable {
    private String uname;
    private Time time;

    public Like(String uname, Time time) {
        this.uname = uname;
        this.time = time;
    }

    public Like() {}

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Like{" +
                "uname='" + uname + '\'' +
                ", time=" + time +
                '}';
    }
}
