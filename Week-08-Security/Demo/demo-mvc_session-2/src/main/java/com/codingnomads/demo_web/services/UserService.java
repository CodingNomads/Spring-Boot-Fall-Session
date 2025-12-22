package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.UserRepository;
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
            throw new IllegalArgumentException("Password must be at least 6 characters long");
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
}
