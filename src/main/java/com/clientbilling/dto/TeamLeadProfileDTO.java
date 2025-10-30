package com.clientbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamLeadProfileDTO {
    private String username;
    private String email;
    private String role;
    private String contactNumber;
    private String profileImage;

}
