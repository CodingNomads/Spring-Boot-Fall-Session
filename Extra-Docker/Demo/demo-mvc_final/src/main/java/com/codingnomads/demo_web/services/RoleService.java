package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.exceptions.RoleNotFoundException;
import com.codingnomads.demo_web.models.Role;
import com.codingnomads.demo_web.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getRoleByName(String role) {
        log.debug("Fetching role by name: {}", role);
        return roleRepository.findByName("ROLE_" + role).orElseThrow(() -> {
            log.error("Role not found: ROLE_{}", role);
            return new RoleNotFoundException("Role not found");
        });
    }

}
