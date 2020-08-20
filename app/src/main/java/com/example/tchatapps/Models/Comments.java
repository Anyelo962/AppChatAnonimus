package com.example.tchatapps.Models;

public class Comments {
    private String comment;
    private String publisher;

    public Comments(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    public Comments(){}

    public String getcomment() {
        return comment;
    }

    public void setUsername(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
