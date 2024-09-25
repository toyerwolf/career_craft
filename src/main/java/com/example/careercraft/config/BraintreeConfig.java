package com.example.careercraft.config;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BraintreeConfig {

    @Bean
    public BraintreeGateway braintreeGateway() {
        return new BraintreeGateway(
                Environment.SANDBOX, // Используйте Environment.PRODUCTION для боевой среды
                "hb4cykjft27xr7nc", // Замените на ваш Merchant ID
                "8d29dc4g4mzccwr5",   // Замените на ваш Public Key
                "ce6f40f3c343ba607d8a14f42257319c"   // Замените на ваш Private Key
        );
    }
}