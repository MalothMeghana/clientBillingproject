package com.clientbilling.service;

import com.clientbilling.model.Admin;
import com.clientbilling.model.Client;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    // ✅ Register Client (with email validation + attach existing Admin)
    public Client registerClient(Client client) {
        // Validate email
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        // Check if email already exists
        Optional<Client> existing = clientRepository.findByEmail(client.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Attach Admin if provided
        if (client.getAdmin() != null && client.getAdmin().getId() != null) {
            Admin existingAdmin = adminRepository.findById(client.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            client.setAdmin(existingAdmin);

            // Maintain bidirectional relationship
            existingAdmin.getClients().add(client);
        }

        return clientRepository.save(client);
    }

    // ✅ Save or Update Client (used in profile upload & updates)
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    // ✅ Get Client by ID
    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    // ✅ Get All Clients
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        clients.forEach(client -> {
            if (client.getAdmin() != null) client.getAdmin().getId(); // initialize relationship
            if (client.getProjects() != null) client.getProjects().size();
            if (client.getInvoices() != null) client.getInvoices().size();
        });
        return clients;
    }

    // ✅ Delete Client by ID
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client not found with ID: " + id);
        }
        clientRepository.deleteById(id);
    }

    // ✅ Optional helper: find by email
    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
}
