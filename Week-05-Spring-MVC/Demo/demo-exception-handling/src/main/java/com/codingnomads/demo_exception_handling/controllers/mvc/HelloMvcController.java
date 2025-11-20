package com.codingnomads.demo_exception_handling.controllers.mvc;

import com.codingnomads.demo_exception_handling.services.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class HelloMvcController {

    private final HelloService helloService;

    @RequestMapping("")
    public String hello(Model model) {

        model.addAttribute("hello", helloService.hello());

        return "hello";
    }

}
