package com.example.tchatker.model;

import java.util.ArrayList;

public class News {
    private Time time;
    private String text;
    private String content;
    private String typeContent;
    private NewsStyle newsStyle;
    private ArrayList<Like> likes;
    private ArrayList<Comment> comments;

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

    @Override
    public String toString() {
        return "News{" +
                "time=" + time +
                ", text='" + text + '\'' +
                ", content='" + content + '\'' +
                ", typeContent='" + typeContent + '\'' +
                ", newsStyle=" + newsStyle +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }
}