package com.example.springjwt.notification;

import com.example.springjwt.User.UserEntity;
import com.example.springjwt.User.UserRepository;
import com.example.springjwt.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FCMController {

    private final UserRepository userRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final JWTUtil jwtUtil;
    private final FCMService fcmService;

    //토큰
    @PostMapping("/token")
    public ResponseEntity<Void> saveToken(@RequestHeader("Authorization") String token,
                                          @RequestBody FcmTokenRequestDTO request) {
        String username = jwtUtil.getUsername(token); // username
        UserEntity user = userRepository.findByUsername(username);

        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setFcmToken(request.getToken());
        deviceToken.setPlatform(request.getPlatform());
        deviceToken.setUser(user);
        deviceToken.setUpdatedAt(LocalDateTime.now());

        deviceTokenRepository.save(deviceToken);
        return ResponseEntity.ok().build();
    }

    //알림전송
    @PostMapping("/send")
    public ResponseEntity<Void> sendCustomNotification(@RequestBody NotificationSendRequestDTO dto) {
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        fcmService.sendNotificationToUser(user, dto.getTitle(), dto.getContent(), dto.getCategory());

        return ResponseEntity.ok().build();
    }

}