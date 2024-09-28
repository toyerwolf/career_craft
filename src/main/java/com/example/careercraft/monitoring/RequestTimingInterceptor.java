package com.example.careercraft.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
@Slf4j
public class RequestTimingInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;



    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        request.setAttribute("startTime", System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        long startTime = (long) request.getAttribute("startTime");
        long duration = System.nanoTime() - startTime;

        Timer timer = Timer.builder("http.requests.timer")
                .tag("method", request.getMethod())
                .tag("uri", request.getRequestURI())
                .description("Timer for HTTP request durations")
                .register(meterRegistry);

        timer.record(duration, java.util.concurrent.TimeUnit.NANOSECONDS);

      log.info("Запрос обработан za: " + duration / 1_000_000 + " мс");
    }
}