package com.example.holing.bounded_context.user.repository;

import com.example.holing.bounded_context.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String socialId);

    Optional<User> findBySocialIdAndProvider(String socialId, String provider);

    Optional<User> findByPublicId(long publicId);
}
