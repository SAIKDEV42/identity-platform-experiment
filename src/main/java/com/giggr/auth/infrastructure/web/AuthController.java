package com.giggr.auth.infrastructure.web;

import com.giggr.auth.application.JwtService;
import com.giggr.auth.application.LoginOtpService;
import com.giggr.auth.domain.RegisteredDevice;
import com.giggr.auth.infrastructure.persistence.RegisteredDeviceRepository;
import com.giggr.identity.application.IdentityService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IdentityService identityService;
    private final JwtService jwtService;
    private final LoginOtpService loginOtpService;
    private final RegisteredDeviceRepository deviceRepository;

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestParam String email) {

        identityService.ensureActive(email);

        loginOtpService.generateOtp(email);

        return ResponseEntity.ok("OTP sent");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        return ResponseEntity.ok("Logged in as: " + authentication.getPrincipal());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        loginOtpService.verifyOtp(email, otp);

        identityService.ensureActive(email);
        String digitalId = identityService.getDigitalId(email);

        String token = jwtService.generateToken(digitalId);

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        String deviceId = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("DEVICE_ID".equals(cookie.getName())) {
                    deviceId = cookie.getValue();
                    break;
                }
            }
        }

        RegisteredDevice device = null;

        if (deviceId != null) {
            device = deviceRepository.findByDeviceId(deviceId).orElse(null);
        }

        if (device == null) {
            deviceId = UUID.randomUUID().toString();

            device = new RegisteredDevice();
            device.setDeviceId(deviceId);
        }

        device.setDigitalId(digitalId);
        device.setIpAddress(ip);
        device.setUserAgent(userAgent);
        device.setLastLoginAt(Instant.now());

        deviceRepository.save(device);

        Cookie sessionCookie = new Cookie("SESSION", token);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(60 * 60 * 24 * 7); // 7 days
        response.addCookie(sessionCookie);

        Cookie deviceCookie = new Cookie("DEVICE_ID", deviceId);
        deviceCookie.setHttpOnly(true);
        deviceCookie.setPath("/");
        deviceCookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
        response.addCookie(deviceCookie);

        return ResponseEntity.ok("Logged in");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("SESSION", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }
}
