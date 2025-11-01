package com.clientbilling.security;

import com.clientbilling.model.Admin;
import com.clientbilling.model.Client;
import com.clientbilling.model.TeamLead;
import com.clientbilling.model.Employee;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.ClientRepository;
import com.clientbilling.repository.TeamLeadRepository;
import com.clientbilling.repository.EmployeeRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TeamLeadRepository teamLeadRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 🔹 1. Admin
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            return buildUser(admin.get().getEmail(), admin.get().getPassword(), admin.get().getRole());
        }

        // 🔹 2. Client
        Optional<Client> client = clientRepository.findByEmail(email);
        if (client.isPresent()) {
            return buildUser(client.get().getEmail(), client.get().getPassword(), client.get().getRole());
        }

        // 🔹 3. TeamLead
        Optional<TeamLead> tl = teamLeadRepository.findByEmail(email);
        if (tl.isPresent()) {
            return buildUser(tl.get().getEmail(), tl.get().getPassword(), tl.get().getRole());
        }

        // 🔹 4. Employee
        Optional<Employee> emp = employeeRepository.findByEmail(email);
        if (emp.isPresent()) {
            return buildUser(emp.get().getEmail(), emp.get().getPassword(), emp.get().getRole());
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    // ✅ Helper to build Spring Security User object
    private UserDetails buildUser(String email, String password, String role) {
        String finalRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        return User.builder()
                   .username(email)
                   .password(password)
                   .roles(finalRole)
                   .build();
    }
}
