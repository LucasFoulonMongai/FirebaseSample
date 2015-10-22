package com.lfm.firesample.models;

/**
 * Created by Lucas FOULON-MONGAÏ, github.com/LucasFoulonMongai  on 21/10/15.
 */
public class Message {
    private String message;
    private String userId;
    private long time;


    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Message() {
    }

    public Message(String userId, long time, String message) {
        this.userId = userId;
        this.time = time;
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}
