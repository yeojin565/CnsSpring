package com.example.springjwt.notification;

import com.example.springjwt.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    List<DeviceToken> findByUser(UserEntity user);

    boolean existsByFcmToken(String fcmToken);
}