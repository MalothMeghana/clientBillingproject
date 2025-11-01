package com.clientbilling.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> otpExpiry = new ConcurrentHashMap<>();

    // Generate 6-digit OTP valid for 5 minutes
    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStorage.put(email, otp);
        otpExpiry.put(email, LocalDateTime.now().plusMinutes(5));
        return otp;
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        LocalDateTime expiry = otpExpiry.get(email);

        if (storedOtp == null || expiry == null) return false;
        if (expiry.isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
            return false;
        }
        return storedOtp.equals(otp);
    }

    // Clear OTP after successful verification
    public void clearOtp(String email) {
        otpStorage.remove(email);
        otpExpiry.remove(email);
    }
}
