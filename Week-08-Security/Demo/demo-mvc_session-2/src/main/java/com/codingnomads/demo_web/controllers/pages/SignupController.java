package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignupController {
    private final UserService userService;

    @GetMapping
    public String signupPage(){
        return "signup";
    }

    @PostMapping
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             Model model) {
        try {
            userService.register(username, password, confirmPassword);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        } catch (DuplicateKeyException e) {
            model.addAttribute("error", "Username already exists");
            return "signup";
        } catch (IllegalStateException e) {
            model.addAttribute("disabled", true);
            model.addAttribute("message", e.getMessage());
            return "signup";
        }

        return "redirect:/login?registered";
    }

}
