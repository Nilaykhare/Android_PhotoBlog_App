package com.example.photoblog.Model_class;

public class MessageRetriving {

    private String message , message_sent_to_user;

    public MessageRetriving() {
    }

    public MessageRetriving(String message, String message_sent_to_user) {
        this.message = message;
        this.message_sent_to_user = message_sent_to_user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_sent_to_user() {
        return message_sent_to_user;
    }

    public void setMessage_sent_to_user(String message_sent_to_user) {
        this.message_sent_to_user = message_sent_to_user;
    }
}
