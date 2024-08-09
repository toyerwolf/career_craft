package com.example.careercraft.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index"; // HTML файл должен быть расположен в src/main/resources/templates/index.html
    }

    @GetMapping("/login.html")
    public String login() {
        return "login"; // HTML файл должен быть расположен в src/main/resources/templates/login.html
    }

    @GetMapping("/register.html")
    public String register() {
        return "register"; // HTML файл должен быть расположен в src/main/resources/templates/register.html
    }
}