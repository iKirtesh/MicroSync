package com.kirtesh.microsync.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomSecurityContextHolder {

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            return ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
        }
        return null; // Handle this case if needed
    }

    // Add other common security-related utility methods here
}
