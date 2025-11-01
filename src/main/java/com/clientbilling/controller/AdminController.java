package com.clientbilling.controller;

import java.io.IOException;
import java.nio.file.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
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

    // ✅ Register Admin — open for the first admin
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        if (adminService.getAllAdmins().isEmpty()) {
            admin.setRole("ROLE_ADMIN");
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            return ResponseEntity.ok(adminService.registerAdmin(admin));
        }

        // All other registrations must be done by Admin only
        String currentRole = securityUtil.getCurrentUserRole();
        if (currentRole == null || !currentRole.equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Access Denied: Only Admin can register another Admin");
        }

        admin.setRole("ROLE_ADMIN");
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return ResponseEntity.ok(adminService.registerAdmin(admin));
    }

    // ✅ Get all admins (only ADMIN can access)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    // ✅ Get single admin by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        Admin admin = adminService.getAdminById(id);
        if (admin != null) return ResponseEntity.ok(admin);
        return ResponseEntity.notFound().build();
    }

    // ✅ Delete admin (only ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Admin deleted successfully");
    }

    // ✅ Upload profile picture (only ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/profile-upload")
    public ResponseEntity<?> uploadProfile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
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
