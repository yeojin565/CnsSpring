package com.example.springjwt.chat.repository;

import com.example.springjwt.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomKeyOrderByIdAsc(String roomKey);

    ChatMessage findTopByRoomKeyOrderByIdDesc(String roomKey);

    int countByRoomKeyAndReceiver_IdAndIsReadFalse(String roomKey, Long receiverId);
}

