package com.example.springjwt.chat.dto;

import java.time.LocalDateTime;

public class ChatRoomResponseDTO {
    private String roomKey;
    private String opponentNickname;
    private String postTitle;
    private String lastMessage;
    private LocalDateTime updatedAt;

    public ChatRoomResponseDTO(String roomKey, String opponentNickname,
                               String postTitle, String lastMessage,
                               LocalDateTime updatedAt) {
        this.roomKey = roomKey;
        this.opponentNickname = opponentNickname;
        this.postTitle = postTitle;
        this.lastMessage = lastMessage;
        this.updatedAt = updatedAt;
    }
}
