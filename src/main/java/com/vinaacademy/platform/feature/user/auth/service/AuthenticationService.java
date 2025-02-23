package com.vinaacademy.platform.feature.user.auth.service;

import com.vinaacademy.platform.exception.UnauthorizedException;
import com.vinaacademy.platform.feature.user.auth.RefreshTokenRepository;
import com.vinaacademy.platform.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

}
