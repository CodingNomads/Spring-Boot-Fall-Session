package com.codingnomads.demo_exception_handling.services;

import com.codingnomads.demo_exception_handling.exceptions.HelloException;
import com.codingnomads.demo_exception_handling.models.Hello;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class HelloService {
    private final Random random = new Random();

    public Hello hello() {
        if (random.nextInt(100) > 80) {
            throw new HelloException("Hello Exception happened!");
        }

        return Hello.builder().message("Hello World!").build();
    }

}
