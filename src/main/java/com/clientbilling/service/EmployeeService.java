package com.clientbilling.service;

import com.clientbilling.model.Admin;
import com.clientbilling.model.TeamLead;
import com.clientbilling.model.Project;
import com.clientbilling.model.Employee;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.TeamLeadRepository;
import com.clientbilling.repository.ProjectRepository;
import com.clientbilling.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TeamLeadRepository teamLeadRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // ✅ Register Employee (with validation + attach existing Admin, TeamLead, Project)
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

        // Attach Admin
        if (employee.getAdmin() != null && employee.getAdmin().getId() != null) {
            Admin admin = adminRepository.findById(employee.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            employee.setAdmin(admin);
            admin.getEmployees().add(employee); // maintain bidirectional link
        }

        // Attach TeamLead
        if (employee.getTeamLead() != null && employee.getTeamLead().getId() != null) {
            TeamLead teamLead = teamLeadRepository.findById(employee.getTeamLead().getId())
                    .orElseThrow(() -> new RuntimeException("TeamLead not found"));
            employee.setTeamLead(teamLead);
            teamLead.getEmployees().add(employee); // maintain bidirectional link
        }

        // Attach Project
        if (employee.getProject() != null && employee.getProject().getId() != null) {
            Project project = projectRepository.findById(employee.getProject().getId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            employee.setProject(project);
            project.getEmployees().add(employee); // maintain bidirectional link
        }

        return employeeRepository.save(employee);
    }

    // ✅ Save or Update Employee (for profile image, edits, etc.)
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // ✅ Get Employee by ID
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    // ✅ Get All Employees (initialize relationships)
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        employees.forEach(emp -> {
            if (emp.getAdmin() != null) emp.getAdmin().getId();
            if (emp.getTeamLead() != null) emp.getTeamLead().getId();
            if (emp.getProject() != null) emp.getProject().getId();
        });
        return employees;
    }

    // ✅ Delete Employee by ID
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

    // ✅ Optional helper: Find Employee by Email
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
}
