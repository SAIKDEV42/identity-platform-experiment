package com.giggr.identity.infrastructure.config;

import com.giggr.identity.domain.identity.DigitalIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class IdentityConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public DigitalIdGenerator digitalIdGenerator(Clock clock) {
        return new DigitalIdGenerator(clock);
    }


}
