package com.clientbilling.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpMail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP - Client Billing System");
        message.setText("Hello,\n\nYour OTP for password reset is: " + otp
                + "\n\nThis OTP is valid for 5 minutes.\n\nRegards,\nClient Billing Team");
        mailSender.send(message);
    }
}
