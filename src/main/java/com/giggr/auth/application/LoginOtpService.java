package com.giggr.auth.application;

import com.giggr.auth.domain.LoginOtp;
import com.giggr.auth.infrastructure.persistence.LoginOtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
@Service
@RequiredArgsConstructor
public class LoginOtpService {

    private final LoginOtpRepository repository;

    public void generateOtp(String email) {

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        LoginOtp loginOtp = new LoginOtp();
        loginOtp.setEmail(email);
        loginOtp.setOtp(otp);
        loginOtp.setExpiresAt(Instant.now().plusSeconds(300)); // 5 mins
        loginOtp.setUsed(false);

        repository.save(loginOtp);

        System.out.println("Login OTP: " + otp); // demo
    }

    public void verifyOtp(String email, String otp) {

        LoginOtp loginOtp = repository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new IllegalStateException("OTP not found"));

        if (loginOtp.isUsed()) {
            throw new IllegalStateException("OTP already used");
        }

        if (loginOtp.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("OTP expired");
        }

        if (!loginOtp.getOtp().equals(otp)) {
            throw new IllegalStateException("Invalid OTP");
        }

        loginOtp.setUsed(true);
        repository.save(loginOtp);
    }
}