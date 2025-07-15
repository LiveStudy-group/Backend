package org.livestudy.security;

import lombok.Getter;
import org.livestudy.domain.user.User;
import org.livestudy.domain.user.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }


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
}
