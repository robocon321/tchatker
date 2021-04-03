package com.example.tchatker.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Time implements Serializable {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public Time(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public Time(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
    }

    public Time() {
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH)+1;
        this.day = calendar.get(Calendar.DATE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
        this.second = calendar.get(Calendar.SECOND);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String toNow() {
        String result = "";

        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH)+1;
        int dayNow = calendar.get(Calendar.DATE);
        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteNow = calendar.get(Calendar.MINUTE);
        int secondNow = calendar.get(Calendar.SECOND);

        if(yearNow - year == 1)
            result = "1 year ago";
        else if(yearNow - year > 1)
            result = (yearNow - year) + " years ago";
        else if(monthNow - month == 1)
            result = "1 month ago";
        else if(monthNow - month > 1 )
            result = (monthNow - month) + " months ago";
        else if(dayNow - day == 1)
            result = "1 day ago";
        else if(dayNow - day > 1)
            result = (dayNow - day) + " days ago";
        else if(hourNow - hour == 1)
            result = "1 hour ago";
        else if(hourNow - hour > 1)
            result = (hourNow - hour)+ " hours ago";
        else if(minuteNow - minute == 1)
            result = "1 minute ago";
        else if(minuteNow - minute > 1)
            result = (minuteNow - minute)+" minutes ago";
        else if(secondNow - second == 1)
            result = "1 second ago";
        else if(secondNow - second == 0)
            result = "just finished";
        else
            result = (secondNow - second) + " seconds ago";

        return result;
    }

    @Override
    public String toString() {
        return this.day+"/"+this.month+"/"+this.year;
    }
}
