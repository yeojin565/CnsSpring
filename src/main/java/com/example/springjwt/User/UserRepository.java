package com.example.springjwt.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Boolean existsByUsername(String username);

    UserEntity findByUsername(String username);

    Optional<UserEntity> findOptionalByUsername(String username);

    // ✅ nickname과 profileImageUrl만 가져오는 projection
    @Query("SELECT u.nickname AS nickname, u.profileImageUrl AS profileImageUrl FROM UserEntity u WHERE u.id = :id")
    UserProjection findProfileById(@Param("id") int id);

}
