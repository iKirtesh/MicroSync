package com.kirtesh.microsync.controller;

import com.kirtesh.microsync.dto.UserDTO;
import com.kirtesh.microsync.model.Address;
import com.kirtesh.microsync.model.SellerProfile;
import com.kirtesh.microsync.model.Users;
import com.kirtesh.microsync.service.AddressService;
import com.kirtesh.microsync.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    private String getCurrentUsername() {
        return com.kirtesh.microsync.util.CustomSecurityContextHolder.getCurrentUsername();
    }



    @PostMapping("/becomeSeller")
    public ResponseEntity<String> createSellerProfile(@RequestBody SellerProfile sellerProfile) {
        String username = getCurrentUsername();
        userService.createSellerProfile(username, sellerProfile);
        return ResponseEntity.ok("You are now a seller but admin approval is required");
    }

    @Operation(summary = "Get User Details", description = "Retrieve details of the currently logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user details",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public ResponseEntity<UserDTO> getUser() {
        String username = getCurrentUsername();
        UserDTO userDTO = userService.getUserDTO(username);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/${username}")
    public ResponseEntity<Optional<Users>> getUsername() {
        String username = getCurrentUsername();
        Optional<Users> user = userService.findByUserName(username);
        return ResponseEntity.ok(user);
    }


    @PutMapping
    public ResponseEntity<String> updateUser(@Valid @RequestBody Users user) {
        String username = getCurrentUsername();
        Users updatedUser = userService.updateUser(username, user, false);
        return ResponseEntity.ok("User updated successfully: " + updatedUser.getUsername());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser() {
        String username = getCurrentUsername();
        return userService.deleteUser(username)
                ? ResponseEntity.ok("User deleted successfully")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PostMapping("/address")
    public ResponseEntity<Address> addAddress(@Valid @RequestBody Address address) {
        String username = getCurrentUsername();
        Address createdAddress = addressService.addAddress(username, address);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @GetMapping("/address")
    public ResponseEntity<List<Address>> getAddressByUser() {
        String username = getCurrentUsername();
        List<Address> addresses = addressService.getAddressesByUsername(username);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/address/default")
    public ResponseEntity<Address> getDefaultAddressByUser() {
        String username = getCurrentUsername();
        return addressService.getAddressesByUsername(username).stream()
                .filter(Address::isDefault)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long addressId, @Valid @RequestBody Address address) {
        String username = getCurrentUsername();
        Address updatedAddress = addressService.updateAddress(username, addressId, address);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        String username = getCurrentUsername();
        addressService.deleteAddress(username, addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/address/{addressId}/default")
    public ResponseEntity<Address> setDefaultAddress(@PathVariable Long addressId) {
        String username = getCurrentUsername();
        Address address = addressService.setDefaultAddress(username, addressId);
        return ResponseEntity.ok(address);
    }
}
