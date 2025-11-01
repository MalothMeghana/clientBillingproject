package com.clientbilling.service;

import com.clientbilling.model.Employee;
import com.clientbilling.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // ✅ Register Employee (simple, since no mapping)
    public Employee registerEmployee(Employee employee) {

        // Validate email
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // Check for duplicate email
        Optional<Employee> existing = employeeRepository.findByEmail(employee.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Default status for new employee
        if (employee.getStatus() == null || employee.getStatus().isEmpty()) {
            employee.setStatus("Active");
        }

        return employeeRepository.save(employee);
    }

    // ✅ Save or Update Employee
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // ✅ Get Employee by ID
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    // ✅ Get All Employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // ✅ Delete Employee
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with ID: " + id);
        }
        employeeRepository.deleteById(id);
    }

    // ✅ Upload Profile Image
    public Employee uploadProfileImage(Long id, MultipartFile file) throws java.io.IOException {
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        // Create uploads folder if missing
        File folder = new File("uploads");
        if (!folder.exists()) folder.mkdirs();

        // Unique file name
        String fileName = "employee_" + id + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // Save image name in DB
        employee.setProfileImage(fileName);
        return employeeRepository.save(employee);
    }

    // ✅ Update Employee Status
    public Employee updateStatus(Long id, String status) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        emp.setStatus(status);
        return employeeRepository.save(emp);
    }

    // ✅ Optional helper
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
}
