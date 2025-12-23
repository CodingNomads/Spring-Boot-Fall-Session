package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.models.Role;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.UserRepository;
import com.codingnomads.demo_web.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("User not found: {}", username);
            return new UsernameNotFoundException("User not found");
        });
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || (auth instanceof AnonymousAuthenticationToken)) {
            log.trace("No authenticated user found in SecurityContext");
            return null;
        }

        User user = (User) auth.getPrincipal();
        log.trace("Current authenticated user: {}", user.getUsername());
        return user;
    }


    public void register(String username, String password, String confirmPassword) {
        log.info("Attempting to register new user: {}", username);
        // Basic validation
        if (!StringUtils.hasText(username)) {
            log.warn("Registration failed: Username is required");
            throw new IllegalArgumentException("Username is required");
        }
        if (!StringUtils.hasText(password)) {
            log.warn("Registration failed for user {}: Password is required", username);
            throw new IllegalArgumentException("Password is required");
        }
        if (confirmPassword != null && !password.equals(confirmPassword)) {
            log.warn("Registration failed for user {}: Passwords do not match", username);
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (password.length() < 3) {
            log.warn("Registration failed for user {}: Password too short", username);
            throw new IllegalArgumentException("Password must be at least 3 characters long");
        }

        // Delegate persistence & uniqueness to UserService
        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("Registration failed: Username {} already exists", username);
            throw new IllegalArgumentException("Username already exists");
        }

        userRepository.save(User.builder().
                username(username).
                password(passwordEncoder.encode(password)).
                roles(Set.of(roleService.getRoleByName("USER"))).
                build());
        log.info("Successfully registered user: {}", username);
    }

    // ADMIN utilities
    public java.util.List<User> findAll() {
        log.debug("Fetching all users (ADMIN)");
        return userRepository.findAll();
    }

    public User findById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User with ID: {} not found", id);
            return new IllegalArgumentException("User not found");
        });
    }

    public java.util.List<Role> getAllRoles() {
        log.debug("Fetching all roles");
        return roleRepository.findAll();
    }

    public java.util.Set<Role> resolveRoles(java.util.List<String> roleNames) {
        log.debug("Resolving roles for names: {}", roleNames);
        if (roleNames == null || roleNames.isEmpty()) {
            return java.util.Set.of();
        }
        java.util.Set<Role> roles = new java.util.HashSet<>();
        for (String rn : roleNames) {
            // Accept both "USER"/"ADMIN" and full "ROLE_USER" forms
            String normalized = rn.startsWith("ROLE_") ? rn : ("ROLE_" + rn);
            roleRepository.findByName(normalized).ifPresent(roles::add);
        }
        log.debug("Resolved {} roles", roles.size());
        return roles;
    }

    public void updateAdminEditableFields(Long id,
                                          boolean accountExpired,
                                          boolean accountLocked,
                                          boolean credentialsExpired,
                                          java.util.Set<Role> roles) {
        log.info("Updating admin-editable fields for user ID: {}", id);
        User user = findById(id);
        user.setAccountExpired(accountExpired);
        user.setAccountLocked(accountLocked);
        user.setCredentialsExpired(credentialsExpired);
        if (roles != null && !roles.isEmpty()) {
            log.debug("Updating roles for user ID: {} to: {}", id, roles);
            user.setRoles(roles);
        }
        userRepository.save(user);
        log.info("Successfully updated user ID: {}", id);
    }
}
