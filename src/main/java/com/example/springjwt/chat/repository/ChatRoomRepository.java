package com.example.springjwt.chat.repository;

import com.example.springjwt.User.UserEntity;
import com.example.springjwt.chat.entity.ChatRoom;
import com.example.springjwt.market.TradePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomKey(String roomKey);

    @Query("SELECT c FROM ChatRoom c WHERE c.buyer = :buyer AND c.seller = :seller AND c.tradePost = :post")
    Optional<ChatRoom> findByBuyerAndSellerAndPost(
            @Param("buyer") UserEntity buyer,
            @Param("seller") UserEntity seller,
            @Param("post") TradePost post
    );

    @Query("SELECT c FROM ChatRoom c WHERE c.buyer = :user OR c.seller = :user ORDER BY c.updatedAt DESC")
    List<ChatRoom> findAllByUser(@Param("user") UserEntity user);
}
