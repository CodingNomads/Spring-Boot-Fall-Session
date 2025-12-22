package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.models.Role;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.services.ApiTokenService;
import com.codingnomads.demo_web.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ApiTokenService apiTokenService;

    // Admin index: show Users and Tokens on a single page
    @GetMapping
    public String index(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("tokens", apiTokenService.listAll());
        return "admin/index";
    }

    // User edit flow
    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", userService.getAllRoles());
        return "admin/user_edit";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam(required = false) boolean accountExpired,
                             @RequestParam(required = false) boolean accountLocked,
                             @RequestParam(required = false) boolean credentialsExpired,
                             @RequestParam(required = false, name = "roles") List<String> roleNames) {
        Set<Role> roles = userService.resolveRoles(roleNames);
        userService.updateAdminEditableFields(id, accountExpired, accountLocked, credentialsExpired, roles);
        return "redirect:/admin";
    }

    // Token actions
    @PostMapping("/tokens/{id}/revoke")
    public String revokeToken(@PathVariable Long id) {
        apiTokenService.revoke(id);
        return "redirect:/admin";
    }

    @PostMapping("/tokens/{id}/delete")
    public String deleteToken(@PathVariable Long id) {
        apiTokenService.delete(id);
        return "redirect:/admin";
    }
}
