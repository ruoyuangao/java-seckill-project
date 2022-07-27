package com.jiuzhang.seckill.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("echo")
public class EchoController {

    // to get the path variable
    @RequestMapping("{text}")
    public String echo(
            @PathVariable("text") String text

    ) {
        return text;
    }
}
