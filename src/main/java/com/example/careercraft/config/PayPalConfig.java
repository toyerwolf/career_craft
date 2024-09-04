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

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        // Ваши клиентские данные
        String clientId = "Aeo1dYj0ZzamXOGhr8kjVcI4WblH7cG1jWx-NBvpSbDJp5JXap5BiGKrzv-s7oqbDcrjVo6v6BbF0s6g";
        String clientSecret = "EO0dGnxT_qhIzKgp8fWxgyCu9-zRsA-n4bg-evaixOwDOfv7P4UVDQkTDUYJ7gpSxC4u1b99PgKRi3bq";

        // Создание конфигурации PayPal
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", "sandbox"); // Или "live" для продакшена
        configMap.put("service.EndPoint", "https://api.sandbox.paypal.com");

        // Получение accessToken
        OAuthTokenCredential authTokenCredential = new OAuthTokenCredential(clientId, clientSecret, configMap);
        String accessToken = authTokenCredential.getAccessToken();

        // Настройка APIContext с полученным accessToken
        APIContext apiContext = new APIContext(accessToken);
        apiContext.setConfigurationMap(configMap);

        return apiContext;
    }
}