package com.codingnomads.demo_web;

import com.codingnomads.demo_web.models.Role;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.repositories.RoleRepository;
import com.codingnomads.demo_web.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

        if (userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(User.builder()
                            .username("user")
//                            .password("{noop}user")
//                            .password("user")
                            .password(passwordEncoder.encode("user"))
                            .accountExpired(false)
                            .accountLocked(false)
                            .credentialsExpired(false)
                            .roles(Set.of(userRole))
                            .build()
            );
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(User.builder()
                            .username("admin")
//                            .password("{noop}admin")
//                            .password("admin")
                            .password(passwordEncoder.encode("admin"))
                            .accountExpired(false)
                            .accountLocked(false)
                            .credentialsExpired(false)
                            .roles(Set.of(userRole, adminRole))
                            .build()
            );
        }
    }
}
