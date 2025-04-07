package com.kirtesh.microsync.controller;

import com.kirtesh.microsync.exception.UserNotFoundException;
import com.kirtesh.microsync.model.Users;
import com.kirtesh.microsync.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        users.forEach(user -> user.setPassword(null)); // Remove passwords from the response
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Users> getUserByUsername(@PathVariable String username) {
        Optional<Users> userOptional = userService.findByUserName(username);

        return userOptional
                .map(user -> {
                    user.setPassword(null); // Remove password from the response
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/user/{username}")
    public ResponseEntity<String> updateUser(@PathVariable String username, @RequestBody Users user) {
        try {
            userService.updateUser(username, user, true);
            return ResponseEntity.ok("User updated successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            userService.deleteUser(username);
            return ResponseEntity.ok("User deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/verifySeller/{username}")
    public ResponseEntity<String> verifySeller(@PathVariable String username) {
        try {
            userService.verifySeller(username, true);
            return ResponseEntity.ok("Seller verified successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
