package com.example.sw;

public class ShowResponse {
    public final String message;

    public static ShowResponse getInstance(String message){
        return new ShowResponse(message);
    }

    ShowResponse(String message){
        this.message = message;
    }
}
