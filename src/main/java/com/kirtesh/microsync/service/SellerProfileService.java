package com.kirtesh.microsync.service;

import com.kirtesh.microsync.dto.SellerProfileDTO;
import com.kirtesh.microsync.exception.UserNotFoundException;
import com.kirtesh.microsync.model.SellerProfile;
import com.kirtesh.microsync.model.Users;
import com.kirtesh.microsync.repository.UserRepository;
import com.kirtesh.microsync.util.CustomSecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SellerProfileService {

    private final UserRepository userRepository;

    public SellerProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SellerProfileDTO getSellerProfile() {
        String username = CustomSecurityContextHolder.getCurrentUsername();
        if (username == null) {
            throw new UserNotFoundException("User not authenticated");
        }

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        SellerProfile sellerProfile = user.getSellerProfile();
        if (sellerProfile == null) {
            throw new UserNotFoundException("Seller profile not found for user: " + username);
        }

        SellerProfileDTO dto = new SellerProfileDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmailVerified(user.getEmailVerified());

        SellerProfileDTO.SellerDetails sellerDetails = new SellerProfileDTO.SellerDetails();
        sellerDetails.setStoreName(sellerProfile.getStoreName());
        sellerDetails.setVerified(sellerProfile.isVerified());

        dto.setSellerDetails(sellerDetails);

        return dto;
    }
}
