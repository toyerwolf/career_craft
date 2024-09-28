package com.example.careercraft.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {

    private final Timer businessProcessTimer;


    @Autowired
    public BusinessService(MeterRegistry meterRegistry) {
        this.businessProcessTimer = Timer.builder("business.process.timer")
                .description("Timer for business process execution time")
                .register(meterRegistry); // Регистрация таймера в MeterRegistry
    }

    public void executeBusinessProcess() {
        businessProcessTimer.record(() -> {
            // Логика основного бизнес-процесса
            try {
                // Имитация долгого процесса
                Thread.sleep(2000); // 2 секунды
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Бизнес-процесс выполнен");
        });
    }
}