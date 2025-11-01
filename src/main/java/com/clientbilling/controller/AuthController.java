package com.clientbilling.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clientbilling.dto.OtpRequestDto;
import com.clientbilling.model.Admin;
import com.clientbilling.model.Client;
import com.clientbilling.model.Employee;
import com.clientbilling.model.TeamLead;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.ClientRepository;
import com.clientbilling.repository.EmployeeRepository;
import com.clientbilling.repository.TeamLeadRepository;
import com.clientbilling.security.CustomUserDetailsService;
import com.clientbilling.security.JwtUtil;
import com.clientbilling.service.MailService;
import com.clientbilling.service.OtpService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private MailService mailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TeamLeadRepository teamLeadRepository;

    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private AdminRepository adminRepository1;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;


    // 🔹 LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String role = userDetails.getAuthorities().stream()
                                     .findFirst()
                                     .map(a -> a.getAuthority())
                                     .orElse("ROLE_USER");

            String token = jwtUtil.generateToken(email, role);

            return ResponseEntity.ok(Map.of(
            	
                "email", email,
                "role", role,
                "token", token
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
    }


    // 🔹 FORGOT PASSWORD - SEND OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        boolean exists = adminRepository.findByEmail(email).isPresent()
                || clientRepository.findByEmail(email).isPresent()
                || employeeRepository.findByEmail(email).isPresent()
                || teamLeadRepository.findByEmail(email).isPresent();

        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No account found with this email");
        }

        String otp = otpService.generateOtp(email);
        mailService.sendOtpMail(email, otp);

        return ResponseEntity.ok("OTP has been sent to your email");
    }


    // 🔹 VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequestDto dto) {
        if (dto.getEmail() == null || dto.getOtp() == null) {
            return ResponseEntity.badRequest().body("Email and OTP are required");
        }

        boolean valid = otpService.verifyOtp(dto.getEmail(), dto.getOtp());
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }

        return ResponseEntity.ok("OTP verified successfully");
    }


    // 🔹 RESET PASSWORD
    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody OtpRequestDto dto) {
        if (dto.getEmail() == null || dto.getOtp() == null || dto.getNewPassword() == null) {
            return ResponseEntity.badRequest().body("Email, OTP, and new password are required");
        }

        boolean valid = otpService.verifyOtp(dto.getEmail(), dto.getOtp());
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
        }

        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

        if (adminRepository.findByEmail(dto.getEmail()).isPresent()) {
            Admin admin = adminRepository.findByEmail(dto.getEmail()).get();
            admin.setPassword(encodedPassword);
            adminRepository.save(admin);
        } else if (clientRepository.findByEmail(dto.getEmail()).isPresent()) {
            Client client = clientRepository.findByEmail(dto.getEmail()).get();
            client.setPassword(encodedPassword);
            clientRepository.save(client);
        } else if (employeeRepository.findByEmail(dto.getEmail()).isPresent()) {
            Employee emp = employeeRepository.findByEmail(dto.getEmail()).get();
            emp.setPassword(encodedPassword);
            employeeRepository.save(emp);
        } else if (teamLeadRepository.findByEmail(dto.getEmail()).isPresent()) {
            TeamLead tl = teamLeadRepository.findByEmail(dto.getEmail()).get();
            tl.setPassword(encodedPassword);
            teamLeadRepository.save(tl);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }

        otpService.clearOtp(dto.getEmail());
        return ResponseEntity.ok("Password reset successful");
    }
}
 