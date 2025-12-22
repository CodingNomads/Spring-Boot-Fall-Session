package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.models.Role;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.UserRepository;
import com.codingnomads.demo_web.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
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
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || (auth instanceof AnonymousAuthenticationToken)) {
            return null;
        }

        return (User) auth.getPrincipal();
    }


    public void register(String username, String password, String confirmPassword) {
        // Basic validation
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Password is required");
        }
        if (confirmPassword != null && !password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (password.length() < 3) {
            throw new IllegalArgumentException("Password must be at least 3 characters long");
        }

        // Delegate persistence & uniqueness to UserService
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        userRepository.save(User.builder().
                username(username).
                password(passwordEncoder.encode(password)).
                roles(Set.of(roleService.getRoleByName("USER"))).
                build());
    }

    // ADMIN utilities
    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public java.util.List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public java.util.Set<Role> resolveRoles(java.util.List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return java.util.Set.of();
        }
        java.util.Set<Role> roles = new java.util.HashSet<>();
        for (String rn : roleNames) {
            // Accept both "USER"/"ADMIN" and full "ROLE_USER" forms
            String normalized = rn.startsWith("ROLE_") ? rn : ("ROLE_" + rn);
            roleRepository.findByName(normalized).ifPresent(roles::add);
        }
        return roles;
    }

    public void updateAdminEditableFields(Long id,
                                          boolean accountExpired,
                                          boolean accountLocked,
                                          boolean credentialsExpired,
                                          java.util.Set<Role> roles) {
        User user = findById(id);
        user.setAccountExpired(accountExpired);
        user.setAccountLocked(accountLocked);
        user.setCredentialsExpired(credentialsExpired);
        if (roles != null && !roles.isEmpty()) {
            user.setRoles(roles);
        }
        userRepository.save(user);
    }
}
