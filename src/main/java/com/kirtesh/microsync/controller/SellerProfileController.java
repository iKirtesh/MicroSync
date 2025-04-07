package com.kirtesh.microsync.controller;

import com.kirtesh.microsync.dto.SellerProfileDTO;
import com.kirtesh.microsync.exception.UserNotFoundException;
import com.kirtesh.microsync.service.SellerProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
@PreAuthorize("hasRole('SELLER')")
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;

    public SellerProfileController(SellerProfileService sellerProfileService) {
        this.sellerProfileService = sellerProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<SellerProfileDTO> getSellerProfile() {
        try {
            SellerProfileDTO sellerProfileDTO = sellerProfileService.getSellerProfile();
            return ResponseEntity.ok(sellerProfileDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
