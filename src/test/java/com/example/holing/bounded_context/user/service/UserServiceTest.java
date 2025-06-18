package com.example.holing.bounded_context.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.holing.bounded_context.auth.dto.OAuthUser;
import com.example.holing.bounded_context.user.entity.Gender;
import com.example.holing.bounded_context.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(UserService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void clean() {
        entityManager.createNativeQuery("ALTER TABLE `user` AUTO_INCREMENT = 1").executeUpdate();
    }

    @Test
    void 새로운_사용자의_경우_기본값으로_저장한다() {
        // given
        OAuthUser ryan = new OAuthUser("thisIsKakaoSocialId", "ryan@example.com", "Ryan", null, "KAKAO");

        // when
        User savedUser = userService.saveOrUpdate(User.from(ryan));

        // then
        assertThat(savedUser.getId()).isEqualTo(1);
        assertThat(savedUser.getEmail()).isEqualTo("ryan@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("Ryan");
        assertThat(savedUser.getPassword()).isEqualTo(null);
        assertThat(savedUser.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(savedUser.getProfileImgUrl()).isEqualTo(null);
        assertThat(savedUser.getProvider()).isEqualTo("KAKAO");
        assertThat(savedUser.getSocialId()).isEqualTo("thisIsKakaoSocialId");
        assertThat(savedUser.getIsPeriod()).isFalse();
        assertThat(savedUser.getIsSelfTested()).isFalse();
        assertThat(savedUser.getIsChanged()).isFalse();
    }

    @Test
    void 소셜_정보가_변경된_경우_업데이트_한다() {
        // given
        OAuthUser ryan = new OAuthUser("thisIsKakaoSocialId", "ryan@example.com", "Ryan", null, "KAKAO");

        // when
        userService.saveOrUpdate(User.from(ryan));
        OAuthUser updatedRyan = new OAuthUser("thisIsKakaoSocialId", "ryan@example.com", "RYAN", "ryan.jpg", "KAKAO");
        User savedUser = userService.saveOrUpdate(User.from(updatedRyan));

        // then
        assertThat(savedUser.getId()).isEqualTo(1);
        assertThat(savedUser.getEmail()).isEqualTo("ryan@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("RYAN");
        assertThat(savedUser.getPassword()).isEqualTo(null);
        assertThat(savedUser.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(savedUser.getProfileImgUrl()).isEqualTo("ryan.jpg");
        assertThat(savedUser.getProvider()).isEqualTo("KAKAO");
        assertThat(savedUser.getSocialId()).isEqualTo("thisIsKakaoSocialId");
        assertThat(savedUser.getIsPeriod()).isFalse();
        assertThat(savedUser.getIsSelfTested()).isFalse();
        assertThat(savedUser.getIsChanged()).isFalse();
    }
}
