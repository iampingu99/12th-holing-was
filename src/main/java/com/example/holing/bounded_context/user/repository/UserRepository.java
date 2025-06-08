package com.example.holing.bounded_context.user.repository;

import com.example.holing.bounded_context.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findBySocialId(String socialId);
}
