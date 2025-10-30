package com.clientbilling.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.clientbilling.dto.AdminProfileDTO;
import com.clientbilling.model.Admin;
import com.clientbilling.security.SecurityUtil;
import com.clientbilling.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ Register Admin
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {

        // Allow first Admin (no authentication required)
        if (adminService.getAllAdmins().isEmpty()) {
            admin.setRole("ROLE_ADMIN");
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            return ResponseEntity.ok(adminService.registerAdmin(admin));
        }

        // Only an existing Admin can create another Admin
        String currentRole = securityUtil.getCurrentUserRole();
        if (currentRole == null || !currentRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied: Only Admin can register another Admin");
        }

        admin.setRole("ROLE_ADMIN");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return ResponseEntity.ok(adminService.registerAdmin(admin));
    }

    // ✅ Get all admins
    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmins() {
        String currentRole = securityUtil.getCurrentUserRole();
        if (currentRole == null || !currentRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied");
        }
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    // ✅ Get single admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        String currentRole = securityUtil.getCurrentUserRole();
        if (currentRole == null || !currentRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        Admin admin = adminService.getAdminById(id);
        if (admin != null) return ResponseEntity.ok(admin);
        return ResponseEntity.notFound().build();
    }

    // ✅ Delete admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        String currentRole = securityUtil.getCurrentUserRole();
        if (currentRole == null || !currentRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Admin deleted successfully");
    }
    // ✅ Upload profile picture
    @PostMapping("/{id}/profile-upload")
    public ResponseEntity<?> uploadProfile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (!securityUtil.hasAnyRole("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return ResponseEntity.status(404).body("Admin not found");
        }

        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

            String filename = "admin_" + id + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            admin.setProfileImage("http://localhost:8080/uploads/" + filename);
            adminService.saveAdmin(admin);

            return ResponseEntity.ok("Profile uploaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }
 // ✅ View Admin Profile
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        if (!securityUtil.hasAnyRole("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied");
        }

        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return ResponseEntity.status(404).body("Admin not found");
        }

        AdminProfileDTO profile = new AdminProfileDTO(
                admin.getUsername(),
                admin.getRole(),
                admin.getEmail(),
                admin.getProfileImage(),
                admin.getContactNumber()
        );

        return ResponseEntity.ok(profile);
    }
}