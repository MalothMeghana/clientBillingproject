package com.clientbilling.service;

import com.clientbilling.model.TeamLead;
import com.clientbilling.repository.TeamLeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TeamLeadService {

    @Autowired
    private TeamLeadRepository teamLeadRepository;

    // ✅ Register TeamLead (no mapping)
    public TeamLead registerTeamLead(TeamLead teamLead) {
        if (teamLead.getEmail() == null || teamLead.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        Optional<TeamLead> existing = teamLeadRepository.findByEmail(teamLead.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        return teamLeadRepository.save(teamLead);
    }

    // ✅ Save or update TeamLead (used in profile upload)
    public TeamLead saveTeamLead(TeamLead teamLead) {
        return teamLeadRepository.save(teamLead);
    }

    // ✅ Get TeamLead by ID
    public TeamLead getTeamLeadById(Long id) {
        return teamLeadRepository.findById(id).orElse(null);
    }

    // ✅ Get all TeamLeads
    public List<TeamLead> getAllTeamLeads() {
        return teamLeadRepository.findAll();
    }

    // ✅ Delete TeamLead
    public void deleteTeamLead(Long id) {
        teamLeadRepository.deleteById(id);
    }
}
