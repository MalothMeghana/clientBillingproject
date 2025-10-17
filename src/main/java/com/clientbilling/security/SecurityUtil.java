package com.clientbilling.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    // Get currently logged-in username from JWT
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    // Get currently logged-in role from JWT
    public String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && !auth.getAuthorities().isEmpty()) {
            GrantedAuthority authority = auth.getAuthorities().iterator().next();
            return authority.getAuthority(); // Example: ROLE_ADMIN, ROLE_CLIENT
        }
        return null;
    }
}
