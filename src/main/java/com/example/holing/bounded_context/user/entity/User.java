package com.example.holing.bounded_context.user.entity;

import com.example.holing.base.BaseTimeEntity;
import com.example.holing.bounded_context.auth.dto.OAuthUser;
import com.example.holing.bounded_context.auth.dto.SignInRequestDto;
import com.example.holing.bounded_context.mission.entity.MissionResult;
import com.example.holing.bounded_context.schedule.entity.Schedule;
import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_public_id", columnNames = {"public_id"}),
                @UniqueConstraint(name = "uk_user_social_id_provider", columnNames = {"social_id", "provider"})
        }
)
public class User extends BaseTimeEntity implements UserDetails {

    @OneToMany(mappedBy = "user")
    List<Schedule> schedules = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private String profileImgUrl;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Boolean isPeriod;

    @Column(nullable = false)
    private int point;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private Boolean isChanged;

    @Column(nullable = false)
    private Boolean isSelfTested;

    @OneToOne
    private User mate;

    @Setter
    @Column(nullable = false, unique = true)
    private long publicId;

    @OneToMany(mappedBy = "user")
    private List<MissionResult> missionResults;

    @Builder
    public User(String email, String password, String nickname, String profileImgUrl, Gender gender, Boolean isPeriod,
                String socialId, String provider) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.gender = gender == null ? Gender.FEMALE : gender;
        this.isPeriod = (gender == Gender.FEMALE) ? isPeriod : false;
        this.point = 0;
        this.socialId = socialId;
        this.provider = provider;
        this.publicId = TsidCreator.getTsid().toLong();
        this.isChanged = false;
        this.isSelfTested = false;
    }

    public static User from(OAuthUser oAuthUser) {
        return User.builder()
                .email(oAuthUser.email())
                .nickname(oAuthUser.nickname())
                .profileImgUrl(oAuthUser.profileImageUrl())
                .socialId(oAuthUser.id())
                .provider(oAuthUser.provider())
                .build();
    }

    public static User of(OAuthUser dto, SignInRequestDto request) {
        return User.builder()
                .email(dto.email())
                .nickname(dto.nickname())
                .profileImgUrl(dto.profileImageUrl())
                .socialId(dto.id())
                .provider(dto.provider())
                .gender(request.gender())
                .isPeriod(request.isPeriod())
                .build();
    }

    public User update(String nickname, String profileImgUrl) {
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        return this;
    }

    public void connectMate(User user) {
        this.mate = user;
        user.mate = this;
    }

    public void disconnectMate(User user) {
        this.mate = null;
        user.mate = null;
    }

    public int addPoint(int point) {
        this.point += point;
        return this.point;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setIsChanged(boolean state) {
        this.isChanged = state;
    }

    public void setIsSelfTested(boolean isSelfTested) {
        this.isSelfTested = isSelfTested;
    }

}
