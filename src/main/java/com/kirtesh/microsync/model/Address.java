package com.kirtesh.microsync.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String flatHouseNo;

    @NotNull
    private String areaStreetLocation;

    private String landmark;

    @NotNull
    private String pincode;

    @NotNull
    private String city;

    @NotNull
    private String state;

    @NotNull
    private String typeName;  // e.g., Home, Work

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String mobile;

    @Email
    private String email;

    private boolean isDefault;

    private boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private Users user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void updateFrom(Address updatedAddress) {
        this.flatHouseNo = updatedAddress.flatHouseNo;
        this.areaStreetLocation = updatedAddress.areaStreetLocation;
        this.landmark = updatedAddress.landmark;
        this.pincode = updatedAddress.pincode;
        this.city = updatedAddress.city;
        this.state = updatedAddress.state;
        this.typeName = updatedAddress.typeName;
        this.firstName = updatedAddress.firstName;
        this.lastName = updatedAddress.lastName;
        this.mobile = updatedAddress.mobile;
        this.email = updatedAddress.email;
        this.isDefault = updatedAddress.isDefault;
        this.isActive = updatedAddress.isActive;
    }
}
