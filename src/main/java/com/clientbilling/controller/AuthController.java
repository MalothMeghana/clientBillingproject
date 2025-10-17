package com.clientbilling.controller;

import com.clientbilling.security.CustomUserDetailsService;
import com.clientbilling.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            // Authenticate user (Spring compares raw password with BCrypt)
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            // Load user details from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Extract role
            String role = userDetails.getAuthorities().stream()
                                     .findFirst()
                                     .map(a -> a.getAuthority())
                                     .orElse("ROLE_USER");

            // Generate JWT
            String token = jwtUtil.generateToken(username, role);

            return ResponseEntity.ok(Map.of(
                "username", username,
                "role", role,
                "token", token
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Credentials"));
        }
    }
}
