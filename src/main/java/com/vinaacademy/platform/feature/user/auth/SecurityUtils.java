package com.vinaacademy.platform.feature.user.auth;

import com.vinaacademy.platform.exception.UnauthorizedException;
import com.vinaacademy.platform.feature.user.UserRepository;
import com.vinaacademy.platform.feature.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityUtils {
    
    private final UserRepository userRepository;
    
    /**
     * Get the currently authenticated user
     * @return The current user
     * @throws UnauthorizedException if no user is authenticated
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }
        
        String email;
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        
        // Based on CustomUserDetailService, we're using email as the username
        return userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
    
    /**
     * Check if the current user has a specific role
     * @param role The role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.getAuthorities().stream()
                   .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
    
    /**
     * Check if the current user has any of the specified roles
     * @param roles The roles to check
     * @return true if the user has any of the roles, false otherwise
     */
    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the email of the current user
     * @return The email of the current user or empty optional if no user is authenticated
     */
    public Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else {
            return Optional.of(principal.toString());
        }
    }
    
    /**
     * Checks if the current user has permission to modify the resource
     * @param resourceAuthorId The ID of the resource author
     * @return true if user is the author or an admin
     */
    @Transactional
    public boolean canModifyResource(UUID resourceAuthorId) {
        try {
            User currentUser = getCurrentUser();
            return currentUser.getId().equals(resourceAuthorId) || 
                   currentUser.getRoles().stream()
                     .anyMatch(role -> "ADMIN".equals(role.getCode()));
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized user attempted to modify resource: {}", e.getMessage());
            return false;
        }
    }
}
