package com.example.sw;

public class SendMessage {
    public final String message;

    public static ShowResponse getInstance(String message){
        return new ShowResponse(message);
    }

    SendMessage(String message){
        this.message = message;
    }
}
