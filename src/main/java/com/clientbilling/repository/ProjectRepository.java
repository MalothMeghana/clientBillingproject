package com.clientbilling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clientbilling.model.Project;


public interface ProjectRepository extends JpaRepository<Project, Long> {
   
    
}