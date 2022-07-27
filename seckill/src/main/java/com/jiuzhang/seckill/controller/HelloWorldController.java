package com.jiuzhang.seckill.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// adding the notation to make it accessible from the outside
@RestController
public class HelloWorldController {

    @RequestMapping
    public String helloWorld() {
        return "Hello, world";
    }
}
