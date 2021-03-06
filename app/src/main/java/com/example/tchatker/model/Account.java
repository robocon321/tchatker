package com.example.tchatker.model;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable{
    private String uname;
    private String pwd;
    private String name;
    private String phoneNumber;
    private String email;
    private Date birthDay;
    private String avatar;
    private String status;

    public Account() {
    }

    public Account(String uname, String pwd, String name, String phoneNumber, String email, Date birthDay, String avatar, String status) {
        this.uname = uname;
        this.pwd = pwd;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDay = birthDay;
        this.avatar = avatar;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    @Override
    public String toString() {
        return "Account{" +
                "uname='" + uname + '\'' +
                ", pwd='" + pwd + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", birthDay=" + birthDay +
                ", avatar='" + avatar + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
