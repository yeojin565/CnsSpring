package com.example.springjwt.User;

import com.example.springjwt.dto.CustomUserDetails;
import com.example.springjwt.dto.LoginInfoResponse;
import com.example.springjwt.dto.UserUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // 사용자 위치 저장
    @PostMapping("/location")
    public ResponseEntity<?> setUserLocation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        int userId = userDetails.getUserEntity().getId();
        userService.updateUserLocation(userId, latitude, longitude);
        return ResponseEntity.ok("위치 저장 완료");
    }

    // 사용자 위치 조회
    @GetMapping("/location")
    public ResponseEntity<?> getUserLocation(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userId = userDetails.getUserEntity().getId();
        UserEntity user = userService.getUserById(userId);

        return ResponseEntity.ok(new UserLocationResponse(user.getLatitude(), user.getLongitude()));
    }

    // DTO 클래스 (내부 클래스 or 별도 파일로 분리 가능)
    record UserLocationResponse(Double latitude, Double longitude) {}

    // 마이페이지 사용자 이름 출력
    @GetMapping("/profile")
    public ResponseEntity<LoginInfoResponse> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserEntity user = userDetails.getUserEntity();
        return ResponseEntity.ok(new LoginInfoResponse(user.getUsername(), user.getName()));
    }

    // 마이페이지 개인정보수정
    @PutMapping("/update")
    public ResponseEntity<?> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdateRequestDTO dto) {

        userService.updateUser(userDetails.getUserEntity().getId(), dto);
        return ResponseEntity.ok("수정 완료");
    }
    @PostMapping("/check-password")
    public ResponseEntity<?> checkPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, String> body
    ) {
        String inputPassword = body.get("password");
        String storedPassword = userDetails.getPassword();

        boolean matches = passwordEncoder.matches(inputPassword, storedPassword);
        return ResponseEntity.ok(matches);
    }

}
