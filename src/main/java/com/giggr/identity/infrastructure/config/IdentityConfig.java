package com.giggr.identity.infrastructure.config;

import com.giggr.identity.domain.identity.DigitalIdGenerator;
import com.giggr.identity.domain.identity.EntityType;
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
        return new DigitalIdGenerator(EntityType.INDIVIDUAL, clock);
    }

//    @Bean
//    CommandLineRunner demo(IdentityService service) {
//        return args -> {
//
//            IdentityProfile profile = new IdentityProfile(
//                    "Demo",
//                    "User",
//                    new Email("demo@test.com"),
//                    "9999999999",
//                    new DateOfBirth(LocalDate.of(1995, 1, 1)),
//                    "UK"
//            );
//
//            OtpCode otp = service.register(profile);
//            service.verify(profile.email(), otp);
//            service.activate(profile.email());
//
//            System.out.println("Inserted demo identity.");
//        };
//    }
}
