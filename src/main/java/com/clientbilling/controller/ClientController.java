package com.clientbilling.controller;

import com.clientbilling.dto.ClientProfileDTO;
import com.clientbilling.model.Client;
import com.clientbilling.service.ClientService;
import com.clientbilling.security.JwtUtil;
import com.clientbilling.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // ✅ Register Client (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> registerClient(@RequestBody Client client) {

        // ✅ Validate adminId
        if (client.getAdminid() == null) {
            return ResponseEntity.badRequest().body("adminId is required for client registration");
        }

        // Default role for registered user
        if (client.getRole() == null || client.getRole().isEmpty()) {
            client.setRole("ROLE_CLIENT");
        }

        // Encrypt password before saving
        client.setPassword(passwordEncoder.encode(client.getPassword()));

        Client savedClient = clientService.registerClient(client);
        return ResponseEntity.ok(savedClient);
    }

    // ✅ Get all clients (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    // ✅ Get client by ID (Admin + Client)
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Long id) {
        Client client = clientService.getClientById(id);
        if (client != null) return ResponseEntity.ok(client);
        return ResponseEntity.notFound().build();
    }

    // ✅ Delete client (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok("Client deleted successfully");
    }

    // ✅ Upload profile image (Admin + Client)
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @PostMapping("/{id}/profile-upload")
    public ResponseEntity<?> uploadProfile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return ResponseEntity.status(404).body("Client not found");
        }

        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

            String filename = "client_" + id + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            client.setProfileImage("http://localhost:8080/uploads/" + filename);
            clientService.saveClient(client);

            return ResponseEntity.ok("Profile uploaded successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    // ✅ Get Client Profile (Admin + Client)
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return ResponseEntity.status(404).body("Client not found");
        }

        ClientProfileDTO profile = new ClientProfileDTO(
                client.getUsername(),
                client.getRole(),
                client.getEmail(),
                client.getProfileImage(),
                client.getContactNumber(),
                client.getCompanyName()
        );

        return ResponseEntity.ok(profile);
    }
}
