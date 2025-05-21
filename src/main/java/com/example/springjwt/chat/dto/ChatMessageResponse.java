package com.example.springjwt.chat.dto;

import java.time.LocalDateTime;

public class ChatMessageResponse {
    private int senderId;
    private String message;
    private LocalDateTime timestamp;

    public ChatMessageResponse(int senderId, String message, LocalDateTime timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
