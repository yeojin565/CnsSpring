package com.example.springjwt.chat.service;

import com.example.springjwt.chat.dto.ChatMessageResponse;
import com.example.springjwt.chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatMessageResponse> getMessagesByRoomKey(String roomKey) {
        return chatMessageRepository.findByRoomKeyOrderByIdAsc(roomKey)
                .stream()
                .map(msg -> new ChatMessageResponse(
                        msg.getSender().getId(),
                        msg.getMessage(),
                        msg.getTimestamp()
                ))
                .collect(Collectors.toList());
    }
}
