package org.livestudy.security;

import lombok.Getter;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@Getter
public class SecurityUser implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    // 일반 로그인용 생성자
    public SecurityUser(User user) {
        this.user = user;
    }

    // OAuth2 로그인용 생성자
    public SecurityUser(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !(user.getUserStatus().equals(UserStatus.PERMANENT_BAN)
                || user.getUserStatus().equals(UserStatus.TEMPORARY_BAN));
    }


    @Override
    public boolean isEnabled() {
        return user.getUserStatus().equals(UserStatus.NORMAL);
    }

    // OAuth2User 인터페이스 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getId().toString();
    }
}
