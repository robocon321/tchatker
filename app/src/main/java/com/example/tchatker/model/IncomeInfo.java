package com.example.tchatker.model;

import java.io.Serializable;

public class IncomeInfo implements Serializable {
    private String peerId;
    private String uname;
    private boolean isVideo;
    private boolean hasConnect;

    public IncomeInfo(){}

    public IncomeInfo(String peerId, String uname, boolean isVideo, boolean hasConnect) {
        this.peerId = peerId;
        this.uname = uname;
        this.isVideo = isVideo;
        this.hasConnect = hasConnect;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean isVideo) {
        isVideo = isVideo;
    }

    public boolean isHasConnect() {
        return hasConnect;
    }

    public void setHasConnect(boolean hasConnect) {
        this.hasConnect = hasConnect;
    }

    @Override
    public String toString() {
        return "IncomeInfo{" +
                "peerId='" + peerId + '\'' +
                ", uname='" + uname + '\'' +
                ", isVideo=" + isVideo +
                ", hasConnect=" + hasConnect +
                '}';
    }
}
