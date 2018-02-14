package com.atribus.bloodbankyrc.Model;

/**
 * Created by root on 14/2/18.
 */
/**
 * Created by Chizoba on 3/26/2017.
 */
public class Message {
    private String title, message;

    public Message(){}

    public Message(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
