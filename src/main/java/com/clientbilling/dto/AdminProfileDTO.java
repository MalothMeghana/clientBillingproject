package com.clientbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminProfileDTO {
    private String username;
    private String role;
    private String email;
    private String profileImage;
    private String contactNumber;// Base64 string
}

