package com.example.tchatker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class News implements Comparable<News>, Serializable {
    private String id;
    private String uname;
    private Time time;
    private String text;
    private String content;
    private String typeContent;
    private NewsStyle newsStyle;
    private ArrayList<Like> likes;
    private ArrayList<Comment> comments;

    public News(String id, String uname, Time time, String text, String content, String typeContent, NewsStyle newsStyle, ArrayList<Like> likes, ArrayList<Comment> comments) {
        this.id = id;
        this.uname = uname;
        this.time = time;
        this.text = text;
        this.content = content;
        this.typeContent = typeContent;
        this.newsStyle = newsStyle;
        this.likes = likes;
        this.comments = comments;
    }

    public News(Time time, String text, String content, String typeContent, NewsStyle newsStyle) {
        this.time = time;
        this.text = text;
        this.content = content;
        this.typeContent = typeContent;
        this.newsStyle = newsStyle;
        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public News(Time time, String text, String content, String typeContent, NewsStyle newsStyle, ArrayList<Like> likes, ArrayList<Comment> comments) {
        this.time = time;
        this.text = text;
        this.content = content;
        this.typeContent = typeContent;
        this.newsStyle = newsStyle;
        this.likes = likes;
        this.comments = comments;
    }

    public News() {
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTypeContent() {
        return typeContent;
    }

    public void setTypeContent(String typeContent) {
        this.typeContent = typeContent;
    }

    public NewsStyle getNewsStyle() {
        return newsStyle;
    }

    public void setNewsStyle(NewsStyle newsStyle) {
        this.newsStyle = newsStyle;
    }

    public ArrayList<Like> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Like> likes) {
        this.likes = likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }


    @Override
    public String toString() {
        return "News{" +
                "id='" + id + '\'' +
                ", uname='" + uname + '\'' +
                ", time=" + time +
                ", text='" + text + '\'' +
                ", content='" + content + '\'' +
                ", typeContent='" + typeContent + '\'' +
                ", newsStyle=" + newsStyle +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }

    @Override
    public int compareTo(News o) {
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
