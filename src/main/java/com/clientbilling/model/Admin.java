

package com.clientbilling.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admins")

public class Admin {

	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long Adminid;

	
	    private String username;
	    private String password;
	    private String contactNumber;

	    @Column(unique = true, nullable = false)
	    private String email;

	    private String role;
	    private String profileImage;

  
}

