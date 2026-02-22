package com.giggr.identity.application.port;

public interface EmailSender {

    void sendOtp(String toEmail, String otp);

    void sendConsentRequest(String parentEmail, String childEmail);

    void sendDigitalId(String toEmail, String digitalId);
}
