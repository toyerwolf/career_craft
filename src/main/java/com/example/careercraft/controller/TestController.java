package com.example.careercraft.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class TestController {

    @GetMapping("/login")
    public String login() {
        // Возвращаем имя HTML-шаблона для страницы входа
        return "login"; // Убедитесь, что у вас есть HTML-шаблон с именем "login.html" в папке templates
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        // Можно использовать информацию о пользователе для дальнейшей логики
        // Например, получить email и перенаправить на страницу профиля

        // Для примера, мы можем добавить атрибуты в модель и вернуть представление профиля
        // В реальном приложении лучше сохранять информацию о пользователе в сессии или в базе данных
        String email = oAuth2User.getAttribute("email"); // Получаем email пользователя

        // Можно добавить email в модель, если нужно использовать в представлении
        // model.addAttribute("email", email);

        // Возвращаем имя HTML-шаблона для страницы после успешного входа
        return "redirect:/home"; // Перенаправляем на главную страницу после успешного входа
    }
}
