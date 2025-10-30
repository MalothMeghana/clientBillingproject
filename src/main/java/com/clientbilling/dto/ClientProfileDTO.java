package com.clientbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfileDTO {
    private String username;
    private String role;
    private String email;
    private String profileImage;
    private String contactNumber;
    private String companyName;
}
