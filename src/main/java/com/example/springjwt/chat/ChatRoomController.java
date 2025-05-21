package com.example.springjwt.chat;

import com.example.springjwt.User.UserEntity;
import com.example.springjwt.User.UserProjection;
import com.example.springjwt.User.UserRepository;
import com.example.springjwt.chat.dto.ChatRoomResponse;
import com.example.springjwt.chat.entity.ChatRoom;
import com.example.springjwt.chat.entity.ChatMessage;
import com.example.springjwt.chat.repository.ChatMessageRepository;
import com.example.springjwt.chat.repository.ChatRoomRepository;
import com.example.springjwt.chat.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat/room")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ChatRoomController(
            ChatRoomService chatRoomService,
            ChatRoomRepository chatRoomRepository,
            ChatMessageRepository chatMessageRepository,
            UserRepository userRepository
    ) {
        this.chatRoomService = chatRoomService;
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<ChatRoom> createChatRoom(
            @RequestParam String buyerUsername,
            @RequestParam Long postId
    ) {
        ChatRoom room = chatRoomService.getOrCreateRoom(buyerUsername, postId);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@PathVariable Long userId) {
        UserEntity me = userRepository.findById(userId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        List<ChatRoom> rooms = chatRoomRepository.findAllByUser(me);

        List<ChatRoomResponse> response = rooms.stream().map(room -> {
            String roomKey = room.getRoomKey();

            ChatMessage lastMessage = chatMessageRepository.findTopByRoomKeyOrderByIdDesc(roomKey);
            String lastMessageContent = (lastMessage != null) ? lastMessage.getMessage() : "";
            String timestamp = (lastMessage != null && lastMessage.getTimestamp() != null)
                    ? lastMessage.getTimestamp().format(formatter)
                    : "";

            // 상대방 정보 조회
            UserEntity opponent = room.getOtherUser(me);
            UserProjection profile = userRepository.findProfileById(opponent.getId());

            String nickname = (profile != null) ? profile.getNickname() : "";
            String profileImageUrl = (profile != null) ? profile.getProfileImageUrl() : "";

            int unreadCount = chatMessageRepository.countByRoomKeyAndReceiver_IdAndIsReadFalse(
                    roomKey, Long.valueOf(me.getId())
            );

            return new ChatRoomResponse(
                    roomKey,
                    lastMessageContent,
                    timestamp,
                    (long) opponent.getId(),
                    nickname,
                    unreadCount,
                    profileImageUrl,
                    room.getTradePost().getTitle()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
