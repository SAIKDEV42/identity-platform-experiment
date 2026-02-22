package com.giggr.identity.infrastructure.email;

import com.giggr.identity.application.port.EmailSender;
import org.springframework.stereotype.Component;

@Component
public class ConsoleEmailSender implements EmailSender {

    @Override
    public void sendOtp(String toEmail, String otp) {
        System.out.println("Sending OTP " + otp + " to " + toEmail);
    }

    @Override
    public void sendConsentRequest(String parentEmail, String childEmail) {
        System.out.println("Sending consent request to " + parentEmail +
                " for child " + childEmail);
    }

    @Override
    public void sendDigitalId(String toEmail, String digitalId) {
        System.out.println("Sending Digital ID " + digitalId +
                " to " + toEmail);
    }
}
