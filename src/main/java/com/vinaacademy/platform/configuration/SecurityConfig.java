package com.vinaacademy.platform.configuration;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.vinaacademy.platform.configuration.auth.CustomAccessDeniedHandler;
import com.vinaacademy.platform.configuration.auth.CustomAuthenticationEntryPoint;
import com.vinaacademy.platform.feature.common.constant.AuthConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final UrlBasedCorsConfigurationSource corsConfigurationSource;

    // Admin API Security
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/admin/**") // Apply to Admin API
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().hasRole(AuthConstants.ADMIN_ROLE) // Admin access only
                )
                .apply(commonSecurityConfig());

        return http.build();
    }

    // Authentication & Public API Security
    @Bean
    @Order(2)
    public SecurityFilterChain publicSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/auth/**", "/api/v1/public/**",
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html") // Apply to Public APIs
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // No authentication required
                )
                .apply(commonSecurityConfig());

        return http.build();
    }

    // User API Security
    @Bean
    @Order(3)
    public SecurityFilterChain userSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Require authentication
                )
                .apply(commonSecurityConfig());

        return http.build();
    }

    // Default Security
    @Bean
    public SecurityFilterChain defaultSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().denyAll() // Deny all undefined requests
                )
                .apply(commonSecurityConfig());

        return http.build();
    }

    private SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> commonSecurityConfig() {
        return new SecurityConfigurer<>() {
            @Override
            public void init(HttpSecurity http) throws Exception {
                http
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless
                        .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
                        .httpBasic(AbstractHttpConfigurer::disable) // Disable Basic Auth
                        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Enable JWT authentication
                        .cors(cors -> cors.configurationSource(corsConfigurationSource))
                        .exceptionHandling(exceptionHandling -> exceptionHandling
                                .accessDeniedHandler(customAccessDeniedHandler) // Custom Access Denied Handler
                                .authenticationEntryPoint(customAuthenticationEntryPoint) // Custom Authentication Entry Point
                        );
            }

            @Override
            public void configure(HttpSecurity http) {
                // No additional configurations required
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
