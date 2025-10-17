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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 🔹 1. Admin
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isPresent()) {
            return buildUser(admin.get().getUsername(), admin.get().getPassword(), admin.get().getRole());
        }

        // 🔹 2. Client
        Optional<Client> client = clientRepository.findByUsername(username);
        if (client.isPresent()) {
            return buildUser(client.get().getUsername(), client.get().getPassword(), client.get().getRole());
        }

        // 🔹 3. TeamLead
        Optional<TeamLead> tl = teamLeadRepository.findByUsername(username);
        if (tl.isPresent()) {
            return buildUser(tl.get().getUsername(), tl.get().getPassword(), tl.get().getRole());
        }

        // 🔹 4. Employee
        Optional<Employee> emp = employeeRepository.findByUsername(username);
        if (emp.isPresent()) {
            return buildUser(emp.get().getUsername(), emp.get().getPassword(), emp.get().getRole());
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }

    // ✅ Helper to clean role
    private UserDetails buildUser(String username, String password, String role) {
        String finalRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        return User.builder()
                   .username(username)
                   .password(password)
                   .roles(finalRole)
                   .build();
    }
}
