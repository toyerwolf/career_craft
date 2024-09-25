package com.example.careercraft.config;


import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PayPalConfig {

    private static final String CLIENT_ID = "AdCxHdEwV8JXylF5rDhxdJTrNDRdWeke603R_HUZw7UZJsIIsrEMbwyQWtIgi7jCLu_LtFVELLobkEx-";
    private static final String CLIENT_SECRET = "EIpmFlOaRhL4Lu4bqnluQzrYu6Yjx2KULzyp98Mlh6vgQY4SO_1VcJeyXca8VKv0XpUQzB2yvkfsICd0";
    private static final String MODE = "live";  // Укажите "live" для продакшена

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        // Конфигурация PayPal
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", MODE);

        // Создание APIContext с clientId и clientSecret
        APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, MODE);
        apiContext.setConfigurationMap(configMap);

        return apiContext;
    }
}