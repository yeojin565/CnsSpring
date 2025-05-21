package com.example.springjwt.chat.service;

import com.example.springjwt.User.UserEntity;
import com.example.springjwt.User.UserRepository;
import com.example.springjwt.chat.dto.ChatRoomResponseDTO;
import com.example.springjwt.chat.entity.ChatRoom;
import com.example.springjwt.chat.repository.ChatRoomRepository;
import com.example.springjwt.market.TradePost;
import com.example.springjwt.market.TradePostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final TradePostRepository tradePostRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           UserRepository userRepository,
                           TradePostRepository tradePostRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.tradePostRepository = tradePostRepository;
    }

    public ChatRoom getOrCreateRoom(String buyerUsername, Long postId) {
        UserEntity buyer = userRepository.findByUsername(buyerUsername);
        TradePost post = tradePostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        UserEntity seller = post.getUser();

        Optional<ChatRoom> existingRoom = chatRoomRepository
                .findByBuyerAndSellerAndPost(buyer, seller, post);

        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        ChatRoom newRoom = new ChatRoom();
        newRoom.setBuyer(buyer);
        newRoom.setSeller(seller);
        newRoom.setTradePost(post);
        newRoom.setRoomKey(UUID.randomUUID().toString());
        newRoom.setUpdatedAt(post.getCreatedAt()); // 또는 LocalDateTime.now()

        return chatRoomRepository.save(newRoom);
    }

    public List<ChatRoomResponseDTO> getRoomsForUser(String username) {
        UserEntity user = userRepository.findByUsername(username);

        return chatRoomRepository.findAllByUser(user).stream()
                .map(room -> new ChatRoomResponseDTO(
                        room.getRoomKey(),
                        room.getOtherUser(user).getNickname(),       // 상대방 닉네임
                        room.getTradePost().getTitle(),             // 게시글 제목
                        room.getLastMessage(),                      // 마지막 메시지
                        room.getUpdatedAt()                         // 마지막 갱신 시각
                ))
                .toList();
    }
}
