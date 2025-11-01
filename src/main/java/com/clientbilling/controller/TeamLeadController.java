package com.clientbilling.controller;

import com.clientbilling.model.TeamLead;
import com.clientbilling.service.TeamLeadService;
import com.clientbilling.security.CustomUserDetailsService;
import com.clientbilling.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

@RestController
@RequestMapping("/api/teamlead")
public class TeamLeadController {

    @Autowired
    private TeamLeadService teamLeadService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ TeamLead Login
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
//        String username = loginData.get("username");
//        String password = loginData.get("password");
//
//        try {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//
//            String role = userDetails.getAuthorities().stream()
//                    .findFirst()
//                    .map(a -> a.getAuthority())
//                    .orElse("ROLE_TEAMLEAD");
//
//            String token = jwtUtil.generateToken(username, role);
//
//            return ResponseEntity.ok(Map.of(
//                    "username", username,
//                    "role", role,
//                    "token", token
//            ));
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(401).body(Map.of("error", "Invalid Credentials"));
//        }
//    }

 // ✅ Register TeamLead (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> registerTeamLead(@RequestBody TeamLead teamLead) {

        // ✅ Validate adminId
        if (teamLead.getAdminid() == null) {
            return ResponseEntity.badRequest().body("adminId is required for TeamLead registration");
        }

        // ✅ Validate clientId (optional)
        if (teamLead.getClientid() == null) {
            teamLead.setClientid(0L); // Default 0 if not yet assigned
        }

        // ✅ Default role
        if (teamLead.getRole() == null || teamLead.getRole().isEmpty()) {
            teamLead.setRole("ROLE_TEAMLEAD");
        }

        // ✅ Encrypt password before saving
        teamLead.setPassword(passwordEncoder.encode(teamLead.getPassword()));

        TeamLead savedTeamLead = teamLeadService.registerTeamLead(teamLead);
        return ResponseEntity.ok(savedTeamLead);
    }

    // ✅ Get All TeamLeads (Admin + TeamLead)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAMLEAD')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllTeamLeads() {
        return ResponseEntity.ok(teamLeadService.getAllTeamLeads());
    }

    // ✅ Get TeamLead by ID (Admin + TeamLead)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAMLEAD')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTeamLeadById(@PathVariable Long id) {
        TeamLead teamLead = teamLeadService.getTeamLeadById(id);
        if (teamLead != null) return ResponseEntity.ok(teamLead);
        return ResponseEntity.notFound().build();
    }

    // ✅ Delete TeamLead (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeamLead(@PathVariable Long id) {
        teamLeadService.deleteTeamLead(id);
        return ResponseEntity.ok("TeamLead deleted successfully");
    }

    // ✅ Upload Profile Image (Admin + TeamLead)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAMLEAD')")
    @PostMapping("/{id}/profile-upload")
    public ResponseEntity<?> uploadProfile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        TeamLead teamLead = teamLeadService.getTeamLeadById(id);
        if (teamLead == null) {
            return ResponseEntity.status(404).body("TeamLead not found");
        }

        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

            String fileName = "teamlead_" + id + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            teamLead.setProfileImage("http://localhost:8080/uploads/" + fileName);
            teamLeadService.saveTeamLead(teamLead);

            return ResponseEntity.ok("Profile uploaded successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    // ✅ Get Profile Info (Admin + TeamLead)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAMLEAD')")
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        TeamLead teamLead = teamLeadService.getTeamLeadById(id);
        if (teamLead == null) {
            return ResponseEntity.status(404).body("TeamLead not found");
        }

        return ResponseEntity.ok(Map.of(
                "username", teamLead.getUsername(),
                "email", teamLead.getEmail(),
                "role", teamLead.getRole(),
                "profileImage", teamLead.getProfileImage()
        ));
    }
}
