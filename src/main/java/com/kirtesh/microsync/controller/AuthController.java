package com.kirtesh.microsync.controller;

import com.kirtesh.microsync.model.Users;
import com.kirtesh.microsync.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Users user) {
        return ResponseEntity.ok(userService.registerNewUser(user));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String otp) {
        return ResponseEntity.ok(userService.verifyEmail(email, otp));
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> loginAsUser(@RequestParam String username, @RequestParam String password) {
        Users user = new Users();
        user.setUsername(username);
        user.setPassword(password);
        String token = userService.verifyLogin(user);
        return "fail".equals(token) ?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed") :
                ResponseEntity.ok(token);
    }

}
