package com.example.holing.bounded_context.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.holing.base.exception.GlobalException;
import com.example.holing.base.id.IdProvider;
import com.example.holing.bounded_context.auth.dto.OAuthUser;
import com.example.holing.bounded_context.user.entity.Gender;
import com.example.holing.bounded_context.user.entity.User;
import com.example.holing.bounded_context.user.exception.UserExceptionCode;
import com.example.holing.bounded_context.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(UserService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        entityManager.createNativeQuery("ALTER TABLE `user` AUTO_INCREMENT = 1").executeUpdate();
    }

    @Nested
    class SaveOrUpdateTests {

        @MockBean
        private IdProvider idProvider;

        @Test
        void 새로운_사용자의_경우_기본값으로_저장한다() {
            // given
            when(idProvider.nextId()).thenReturn(12345L);
            OAuthUser ryan = new OAuthUser("thisIsKakaoSocialId", "ryan@example.com", "Ryan", null, "KAKAO");

            // when
            User savedUser = userService.saveOrUpdate(User.from(ryan));

            // then
            assertThat(savedUser.getId()).isEqualTo(1);
            assertThat(savedUser.getPublicId()).isEqualTo(12345L);
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
            when(idProvider.nextId()).thenReturn(12345L);
            OAuthUser ryan = new OAuthUser("thisIsKakaoSocialId", "ryan@example.com", "Ryan", null, "KAKAO");

            // when
            userService.saveOrUpdate(User.from(ryan));
            when(idProvider.nextId()).thenReturn(67890L);
            OAuthUser updatedRyan = new OAuthUser("thisIsKakaoSocialId", "ryan@example.com", "RYAN", "ryan.jpg",
                    "KAKAO");
            User savedUser = userService.saveOrUpdate(User.from(updatedRyan));

            // then
            assertThat(savedUser.getId()).isEqualTo(1);
            assertThat(savedUser.getPublicId()).isEqualTo(12345L);
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


    @Nested
    class ConnectMateTests {

        private User frodo, neo, con;

        @BeforeEach
        void setUp() {
            frodo = User.builder()
                    .email("frodo@example.com")
                    .nickname("Frodo")
                    .profileImgUrl(null)
                    .socialId("thisIsFrodoKakaoId")
                    .provider("KAKAO").build();

            neo = User.builder()
                    .email("neo@email.com")
                    .nickname("Neo")
                    .profileImgUrl(null)
                    .socialId("thisIsNeoKakaoId")
                    .provider("KAKAO").build();

            con = User.builder()
                    .email("con@email.com")
                    .nickname("Con")
                    .profileImgUrl(null)
                    .socialId("thisIsConKakaoId")
                    .provider("KAKAO").build();

            frodo.setPublicId(12345L);
            neo.setPublicId(67890L);
            con.setPublicId(11111L);

            userRepository.save(frodo);
            userRepository.save(neo);
            userRepository.save(con);

            entityManager.flush();
            entityManager.clear();
        }


        @Test
        void 공개_아이디로_짝꿍_연결_성공() {
            // when
            userService.connectMate(frodo.getId(), neo.getPublicId());
            User findFrodo = userRepository.findByPublicId(frodo.getPublicId()).get();
            User findNeo = userRepository.findByPublicId(neo.getPublicId()).get();

            // then
            assertThat(findFrodo.getMate()).isEqualTo(findNeo);
            assertThat(findNeo.getMate()).isEqualTo(findFrodo);
        }

        @Test
        void 짝꿍_연결시_공개_아이디가_존재하지_않는_경우_예외가_발생한다() {
            assertThatThrownBy(() -> userService.connectMate(frodo.getId(), 99999L))
                    .isInstanceOf(GlobalException.class)
                    .hasMessage(UserExceptionCode.TARGET_NOT_FOUND.getCause());
        }

        @Test
        void 짝꿍_연결시_본인_또는_대상의_짝꿍이_이미_존재하는_경우_예외가_발생한다() {
            // when
            userService.connectMate(frodo.getId(), neo.getPublicId());

            // then
            assertThatThrownBy(() -> userService.connectMate(frodo.getId(), con.getPublicId()))
                    .isInstanceOf(GlobalException.class)
                    .hasMessage(UserExceptionCode.USER_MATE_EXISTS.getCause());

            assertThatThrownBy(() -> userService.connectMate(con.getId(), neo.getPublicId()))
                    .isInstanceOf(GlobalException.class)
                    .hasMessage(UserExceptionCode.TARGET_MATE_EXISTS.getCause());
        }
    }
}
