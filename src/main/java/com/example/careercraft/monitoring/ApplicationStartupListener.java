package com.example.careercraft.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j

public class ApplicationStartupListener implements ApplicationListener<ApplicationStartedEvent> {

    private  MeterRegistry meterRegistry;



    @Override
    public void onApplicationEvent(@NotNull ApplicationStartedEvent event) {
        Timer timer = Timer.builder("application.startup.timer")
                .description("Timer for measuring application startup time")
                .register(meterRegistry);

        timer.record(() -> {
            // Логика старта приложения, или можно просто оставить пустым
           log.info("Приложение успешно запущено!");
        });
    }

}