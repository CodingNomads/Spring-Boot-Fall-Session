package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.models.Role;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.services.RoleService;
import com.codingnomads.demo_web.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Deprecated: merged into AdminController under /admin.
 * Keeping the class non-annotated to avoid component scanning.
 */
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;

    // moved to AdminController
}
