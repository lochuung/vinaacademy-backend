package com.vinaacademy.platform.feature.user.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.entity.User;
import com.vinaacademy.platform.feature.user.role.entity.Permission;
import com.vinaacademy.platform.feature.user.role.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 15; // 15 minutes

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> BadRequestException.message("User is invalid."));
        if (!user.isActive() && isLockTimeExpired(user)) {
            unlockAccount(user);
        }

        String[] roles = Optional.of(user.getRole()).map(v -> new String[]{v.getName()}).orElse(new String[]{});
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(!user.isActive()).roles(roles)
                .authorities(getAuthorities(user.getRole())).build();
    }

    public void increaseFailedAttempts(String username) {
        User user = userRepository.findByEmail(username).orElse(null);
        if (user == null) {
            return;
        }
        int newFailedAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newFailedAttempts);

        if (newFailedAttempts >= MAX_FAILED_ATTEMPTS) {
            lockAccount(user);
        } else {
            userRepository.save(user);
        }
    }

    public void resetFailedAttempts(String username) {
        User user = userRepository.findByEmail(username).orElse(null);
        if (user == null) {
            return;
        }
        user.setFailedAttempts(0);
        userRepository.save(user);
    }

    private void lockAccount(User user) {
        user.setActive(false);
        user.setLockTime(LocalDateTime.now().plusMinutes(LOCK_TIME_DURATION));
        userRepository.save(user);
    }

    private boolean isLockTimeExpired(User user) {
        LocalDateTime lockTime = user.getLockTime();
        return lockTime != null && lockTime.isBefore(LocalDateTime.now());
    }

    private void unlockAccount(User user) {
        user.setActive(true);
        user.setFailedAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }


    private Collection<? extends GrantedAuthority> getAuthorities(final Role roles) {
//        return roles.stream().map(s -> new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                return "ROLE_" + s.getName();
//            }
//        }).collect(Collectors.toList());
        return getGrantedAuthorities(getPermissions(roles));
    }

    private Set<String> getPermissions(final Role role) {
        final Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_" + role.getName());
        final List<Permission> collection = new ArrayList<>(role.getPermissions());
        for (final Permission item : collection) {
            permissions.add(item.getName());
        }
        return permissions;
    }

    private List<GrantedAuthority> getGrantedAuthorities(final Set<String> privileges) {
        return privileges.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//         List<GrantedAuthority> authorities = new ArrayList<>();
//        for (final String privilege : privileges) {
//            authorities.add(new SimpleGrantedAuthority(privilege));
//        }
//        return authorities;

    }
}
