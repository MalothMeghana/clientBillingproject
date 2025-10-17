package com.clientbilling.service;

import com.clientbilling.model.Admin;
import com.clientbilling.model.Client;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    public Client registerClient(Client client) {

        // Attach existing Admin
        if (client.getAdmin() != null && client.getAdmin().getId() != null) {
            Admin existingAdmin = adminRepository.findById(client.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            client.setAdmin(existingAdmin);

            // ✅ Maintain bidirectional relationship
            existingAdmin.getClients().add(client);
        }

        return clientRepository.save(client);
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
