package com.codingnomads.demo_web.services;

import com.codingnomads.demo_web.exceptions.RoleNotFoundException;
import com.codingnomads.demo_web.models.Role;
import com.codingnomads.demo_web.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getRoleByName(String role) {
        return roleRepository.findByName("ROLE_" + role).orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

}
