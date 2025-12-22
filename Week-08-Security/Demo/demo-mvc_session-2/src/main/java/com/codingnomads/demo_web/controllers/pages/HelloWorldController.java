package com.codingnomads.demo_web.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class HelloWorldController {

    @GetMapping("/helloWorld")
    public String helloWorld(Model model) {

        String message = "Hello World! (" + LocalDateTime.now() + ")";

        model.addAttribute("messageOnPage", message);

        return "hello_world";
    }

}
