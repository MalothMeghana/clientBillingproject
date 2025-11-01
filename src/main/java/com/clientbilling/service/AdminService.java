package com.clientbilling.service;

import com.clientbilling.model.Admin;
import com.clientbilling.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    /**
     * ✅ Register a new Admin after validating email.
     */
    public Admin registerAdmin(Admin admin) {
        validateEmail(admin.getEmail());

        // Check for existing email
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + admin.getEmail());
        }

        return adminRepository.save(admin);
    }

    /**
     * ✅ Save or update Admin.
     */
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    /**
     * ✅ Get Admin by ID.
     */
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + id));
    }

    /**
     * ✅ Get all Admins.
     */
    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    /**
     * ✅ Delete Admin by ID.
     */
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Admin not found with ID: " + id);
        }
        adminRepository.deleteById(id);
    }

    /**
     * ✅ Find Admin by email.
     */
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    /**
     * 🔹 Utility: Validate email.
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
    }
}
