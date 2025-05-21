package com.example.springjwt.point;

import com.example.springjwt.User.UserEntity;
import com.example.springjwt.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicy pointPolicy;

    // 포인트 적립
    public void addPoint(UserEntity user, PointActionType action, int count, String description) {
        int point = pointPolicy.calculatePoint(action, count);

        if (point == 0) return; // 지급할 포인트가 없으면 무시

        user.setPoint(user.getPoint() + point);
        userRepository.save(user);

        PointHistory history = PointHistory.builder()
                .user(user)
                .action(action)
                .pointChange(point)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        pointHistoryRepository.save(history);
    }

    // 포인트 사용 (차감)
    public void usePoint(UserEntity user, int amount, String description) {
        if (user.getPoint() < amount) {
            throw new IllegalArgumentException("보유 포인트가 부족합니다.");
        }

        user.setPoint(user.getPoint() - amount);
        userRepository.save(user);

        PointHistory history = PointHistory.builder()
                .user(user)
                .action(null) // 사용은 특정 액션이 아닐 수 있음
                .pointChange(-amount)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        pointHistoryRepository.save(history);
    }

    // 포인트 이력 조회
    public List<PointHistory> getHistory(int userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return pointHistoryRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
