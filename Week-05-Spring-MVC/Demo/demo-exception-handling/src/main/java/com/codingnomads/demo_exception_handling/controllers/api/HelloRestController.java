package com.codingnomads.demo_exception_handling.controllers.api;

import com.codingnomads.demo_exception_handling.models.Hello;
import com.codingnomads.demo_exception_handling.services.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HelloRestController {

    private final HelloService helloService;

    @RequestMapping("/hello")
    public Hello hello() {
        return helloService.hello();
    }

}
