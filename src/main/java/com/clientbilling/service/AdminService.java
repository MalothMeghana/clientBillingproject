package com.clientbilling.service;

import com.clientbilling.model.Admin;
import com.clientbilling.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    // ✅ Register Admin with email validation
    public Admin registerAdmin(Admin admin) {
        // Validate email
        if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // Check for uniqueness
        Optional<Admin> existing = adminRepository.findByEmail(admin.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        return adminRepository.save(admin);
    }

    // ✅ Save or update Admin (used in profile upload and general updates)
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // ✅ Get Admin by ID
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    // ✅ Get all Admins (initialize lazy relationships to avoid LazyInitializationException)
    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        admins.forEach(admin -> {
            if (admin.getClients() != null) admin.getClients().size();
            if (admin.getProjects() != null) admin.getProjects().size();
            if (admin.getInvoices() != null) admin.getInvoices().size();
        });
        return admins;
    }

    // ✅ Delete Admin by ID
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("Admin not found with ID: " + id);
        }
        adminRepository.deleteById(id);
    }

    // ✅ Optional helper — find by email (useful for login or checks)
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
}
