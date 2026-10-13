package com.example.scamorsafe;

public class Question {
    public final String type;      // SMS, EMAIL or URL
    public final String content;
    public final boolean scam;     // true = scam, false = safe
    public final String reason;

    public Question(String type, String content, boolean scam, String reason) {
        this.type = type;
        this.content = content;
        this.scam = scam;
        this.reason = reason;
    }
}