package com.androidapptraining.Modal;

public class Message {
    String message, sendBy;
    public static String SEND_BY_USER = "user";
    public static String SEND_BY_BOT = "bot";

    public Message(String message, String sendBy) {
        this.message = message;
        this.sendBy = sendBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }
}
