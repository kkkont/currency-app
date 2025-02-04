package com.currency_app.currency_app_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/currency-app")
public class TestController {

    @GetMapping("/hello")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}
