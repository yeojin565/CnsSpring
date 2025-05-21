package com.example.springjwt.chat;

import com.example.springjwt.chat.dto.ChatMessageResponse;
import com.example.springjwt.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/messages/{roomKey}")
    public ResponseEntity<List<ChatMessageResponse>> getChatMessages(@PathVariable String roomKey) {
        return ResponseEntity.ok(chatService.getMessagesByRoomKey(roomKey));
    }
}
