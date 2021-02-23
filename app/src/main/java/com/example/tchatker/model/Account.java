package com.example.tchatker.model;

import java.util.Date;

public class Account {
    private String uname;
    private String pwd;
    private String phoneNumber;
    private String email;
    private Date birthDay;

    public Account() {
    }

    public Account(String uname, String pwd, String phoneNumber, String email, Date birthDay) {
        this.uname = uname;
        this.pwd = pwd;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDay = birthDay;
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
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", birthDay=" + birthDay +
                '}';
    }
}