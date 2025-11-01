package com.clientbilling.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.clientbilling.model.PasswordResetOTP;

import java.util.Optional;

public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {
    Optional<PasswordResetOTP> findByEmail(String email);
    Optional<PasswordResetOTP> findByEmailAndOtp(String email, String otp);
}
