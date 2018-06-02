package com.example.tomer.dogsproject.Hemi;

public class MyMessage {
    String sender;
    String message;
    String image;

    public MyMessage() {
    }

    public MyMessage(String sender, String message, String image) {
        this.sender = sender;
        this.message = message;
        this.image = image;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
