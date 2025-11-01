package com.clientbilling.service;

import com.clientbilling.model.Project;
import com.clientbilling.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public Project addProject(Project project) {
        // No mapping to Admin or Client — pure save
        if (project.getProjectName() == null || project.getProjectName().trim().isEmpty()) {
            throw new RuntimeException("Project name is required");
        }
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}
