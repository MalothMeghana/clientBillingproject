package com.clientbilling.service;

import com.clientbilling.model.Project;
import com.clientbilling.model.Admin;
import com.clientbilling.model.Client;
import com.clientbilling.repository.ProjectRepository;
import com.clientbilling.repository.AdminRepository;
import com.clientbilling.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    public Project addProject(Project project) {

        // Attach existing Admin
        if (project.getAdmin() != null && project.getAdmin().getId() != null) {
            Admin existingAdmin = adminRepository.findById(project.getAdmin().getId())
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            project.setAdmin(existingAdmin);

            // Maintain bidirectional link
            existingAdmin.getProjects().add(project);
        }

        // Attach existing Client
        if (project.getClient() != null && project.getClient().getId() != null) {
            Client existingClient = clientRepository.findById(project.getClient().getId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            project.setClient(existingClient);

            // Maintain client-project link
            existingClient.getProjects().add(project);
        }

        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
