package com.kirtesh.microsync.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerProfileDTO {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean emailVerified;
    private SellerDetails sellerDetails;

    @Getter
    @Setter
    public static class SellerDetails {
        private String storeName;
        private boolean verified;
    }
}
