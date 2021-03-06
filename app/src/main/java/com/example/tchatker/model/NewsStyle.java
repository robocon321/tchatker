package com.example.tchatker.model;

import java.io.Serializable;

public class NewsStyle implements Serializable {
    private String background;
    private String color;

    public NewsStyle(String background, String color) {
        this.background = background;
        this.color = color;
    }

    public NewsStyle() {
        this.background = "#ffffff";
        this.color = "#000000";
    }

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
