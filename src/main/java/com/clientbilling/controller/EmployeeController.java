package com.clientbilling.controller;

import com.clientbilling.dto.EmployeeProfileDTO;
import com.clientbilling.model.Employee;
import com.clientbilling.service.EmployeeService;
import com.clientbilling.security.JwtUtil;
import com.clientbilling.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ Register Employee (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> registerEmployee(@RequestBody Employee employee) {
        // Validate Adminid (mandatory for registration)
        if (employee.getAdminid() == null) {
            return ResponseEntity.badRequest().body("Adminid is required for employee registration");
        }

        // Default role if not provided
        if (employee.getRole() == null || employee.getRole().isEmpty()) {
            employee.setRole("ROLE_EMPLOYEE");
        }

        // Encrypt password before saving
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        Employee savedEmployee = employeeService.registerEmployee(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    // ✅ Get All Employees (Admin, TeamLead, Employee)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAMLEAD', 'EMPLOYEE')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // ✅ Get Employee by ID (Admin, TeamLead, Employee)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAMLEAD', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        Employee emp = employeeService.getEmployeeById(id);
        if (emp != null) return ResponseEntity.ok(emp);
        return ResponseEntity.notFound().build();
    }

    // ✅ Update Employee Status (Admin + TeamLead)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAMLEAD')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Employee emp = employeeService.updateStatus(id, status);
        if (emp != null) return ResponseEntity.ok(emp);
        return ResponseEntity.notFound().build();
    }

    // ✅ Delete Employee (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }

    // ✅ Upload Profile Image (Admin, TeamLead, Employee)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAMLEAD', 'EMPLOYEE')")
    @PostMapping("/{id}/profile-upload")
    public ResponseEntity<?> uploadProfile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Employee emp = employeeService.getEmployeeById(id);
        if (emp == null) {
            return ResponseEntity.status(404).body("Employee not found");
        }

        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

            String filename = "employee_" + id + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            emp.setProfileImage("http://localhost:8080/uploads/" + filename);
            employeeService.saveEmployee(emp);

            return ResponseEntity.ok(Map.of(
                    "message", "Profile uploaded successfully",
                    "profileImage", emp.getProfileImage()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }

    // ✅ Get Employee Profile (DTO) — Admin, TeamLead, Employee
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAMLEAD', 'EMPLOYEE')")
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        Employee emp = employeeService.getEmployeeById(id);
        if (emp == null) {
            return ResponseEntity.status(404).body("Employee not found");
        }

        EmployeeProfileDTO profile = new EmployeeProfileDTO(
                emp.getUsername(),
                emp.getRole(),
                emp.getEmail(),
                emp.getProfileImage(),
                emp.getContactNumber(),
                emp.getStatus()
        );

        return ResponseEntity.ok(profile);
    }
}
