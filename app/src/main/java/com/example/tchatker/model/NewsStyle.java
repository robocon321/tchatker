package com.example.tchatker.model;

public class NewsStyle {
    private String background;
    private String color;

    public NewsStyle(String background, String color) {
        this.background = background;
        this.color = color;
    }

    public NewsStyle() {}

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "NewsStyle{" +
                "background='" + background + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
