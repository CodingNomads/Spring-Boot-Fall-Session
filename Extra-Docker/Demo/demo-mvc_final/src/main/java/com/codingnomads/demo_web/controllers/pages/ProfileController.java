package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.models.ApiToken;
import com.codingnomads.demo_web.models.User;
import com.codingnomads.demo_web.services.ApiTokenService;
import com.codingnomads.demo_web.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final ApiTokenService apiTokenService;

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        if (user != null) {
            List<ApiToken> tokens = apiTokenService.userTokens(user.getId());
            model.addAttribute("tokens", tokens);
        }
        return "profile";
    }

    @PostMapping("/profile/token")
    public String generateToken(@RequestParam(value = "ttlHours", required = false) Integer ttlHours) {
        User user = userService.getCurrentUser();
        if (user != null) {
            java.time.Duration ttl;
            if (ttlHours == null) {
                ttl = java.time.Duration.ofHours(24);
            } else {
                int hours = Math.max(1, Math.min(24, ttlHours));
                ttl = java.time.Duration.ofHours(hours);
            }
            apiTokenService.generate(user, ttl);
        }
        return "redirect:/profile";
    }

    @PostMapping("/profile/token/{id}/delete")
    public String deleteToken(@PathVariable("id") Long id) {
        User user = userService.getCurrentUser();
        if (user != null && id != null) {
            // Ensure the token belongs to the current user before deleting
            boolean ownsToken = apiTokenService.userTokens(user.getId())
                    .stream()
                    .anyMatch(t -> id.equals(t.getId()));
            if (ownsToken) {
                apiTokenService.delete(id);
            }
        }
        return "redirect:/profile";
    }
}
