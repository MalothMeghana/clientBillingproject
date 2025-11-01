package com.clientbilling.controller;

import com.clientbilling.model.Project;
import com.clientbilling.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // ✅ Add Project (Admin, Client, TeamLead)
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'TEAMLEAD')")
    @PostMapping("/add")
    public ResponseEntity<?> addProject(@RequestBody Project project) {
        try {
            // Basic validation (optional)
            if (project.getAdminid() == null || project.getClientid() == null) {
                return ResponseEntity.badRequest().body("Adminid and Clientid are required to create a project");
            }

            Project savedProject = projectService.addProject(project);
            return ResponseEntity.ok(savedProject);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ✅ View all Projects (All roles)
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'TEAMLEAD', 'EMPLOYEE')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // ✅ View Project by ID (All roles)
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'TEAMLEAD', 'EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        if (project != null) {
            return ResponseEntity.ok(project);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Delete Project (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project deleted successfully");
    }
}
