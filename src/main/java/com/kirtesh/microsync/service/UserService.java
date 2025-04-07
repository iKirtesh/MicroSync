package com.kirtesh.microsync.service;

import com.kirtesh.microsync.dto.UserDTO;
import com.kirtesh.microsync.exception.UserNotFoundException;
import com.kirtesh.microsync.exception.UsernameNotFoundException;
import com.kirtesh.microsync.model.Role;
import com.kirtesh.microsync.model.SellerProfile;
import com.kirtesh.microsync.model.Token;
import com.kirtesh.microsync.model.Users;
import com.kirtesh.microsync.repository.RoleRepository;
import com.kirtesh.microsync.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final JWTService jwtService;
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(JWTService jwtService, AuthenticationManager authManager,
                       UserRepository userRepository, EmailService emailService, TokenService tokenService, RoleRepository roleRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String registerNewUser(Users user) {
        Optional<Users> existingUserByUsernameOpt = userRepository.findByUsername(user.getUsername());
        if (existingUserByUsernameOpt.isPresent()) {
            Users existingUser = existingUserByUsernameOpt.get();
            if (!existingUser.getEmailVerified()) {
                // Email is not verified, resend OTP
                Token otpToken = tokenService.createToken(existingUser.getUsername(), "EMAIL_VERIFICATION_OTP");
                try {
                    emailService.sendOtpEmail(existingUser.getEmail(), existingUser.getFirstName(), existingUser.getLastName(), otpToken.getToken());
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return "Error sending OTP email. Please try again later.";
                }
                return "OTP has been resent to your email.";
            } else {
                return "Username already exists. Please choose a different one.";
            }
        }

        Optional<Users> existingUserByEmailOpt = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmailOpt.isPresent()) {
            Users existingUser = existingUserByEmailOpt.get();
            if (existingUser.getEmailVerified()) {
                return "This Email already exists. Please login.";
            } else {
                Token otpToken = tokenService.createToken(existingUser.getUsername(), "EMAIL_VERIFICATION_OTP");
                try {
                    emailService.sendOtpEmail(existingUser.getEmail(), existingUser.getFirstName(), existingUser.getLastName(), otpToken.getToken());
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return "Error sending OTP email. Please try again later.";
                }
                return "Email is already in use but not verified. OTP has been resent.";
            }
        }

        // Register new user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);
        user.setRoles(Set.of(roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"))));

        userRepository.save(user);

        Token otpToken = tokenService.createToken(user.getUsername(), "EMAIL_VERIFICATION_OTP");
        try {
            emailService.sendOtpEmail(user.getEmail(), user.getFirstName(), user.getLastName(), otpToken.getToken()); // Send OTP to new user
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error sending OTP email. Please try again later.";
        }
        return "User registered successfully. Please verify your email.";
    }



    @Transactional
    public String verifyEmail(String email, String otp) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (tokenService.validateToken(otp, user.getUsername(), "EMAIL_VERIFICATION_OTP")) {
            if (!user.getEmailVerified()) {
                user.setEmailVerified(true);
                userRepository.save(user);
                tokenService.deleteToken(user.getUsername(), "EMAIL_VERIFICATION_OTP");
                return "Email verified successfully";
            } else {
                return "Email is already verified";
            }
        }
        return "Invalid or expired OTP";
    }

    public String verifyLogin(Users user) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        return authentication.isAuthenticated() ? jwtService.generateToken(user.getUsername()) : "fail"; // 1 hour
    }


    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO getUserDTO(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmailVerified(user.getEmailVerified());

        return userDTO;
    }


    public Optional<Users> findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public Users updateUser(String username, Users user, boolean isAdmin) {
        Users existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        existingUser.updateFrom(user);

        if (isAdmin || user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public boolean deleteUser(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
        return false;
    }

    @Transactional
    public void createSellerProfile(String username, SellerProfile sellerProfile) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getSellerProfile() != null) {
            throw new RuntimeException("User is already a seller");
        }

        sellerProfile.setUser(user);
        user.setSellerProfile(sellerProfile);

        user.getRoles().add(roleRepository.findByName("SELLER")
                .orElseThrow(() -> new RuntimeException("SELLER role not found")));

        userRepository.save(user);
    }

    @Transactional
    public void verifySeller(String username, boolean isAdmin) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        SellerProfile sellerProfile = user.getSellerProfile();

        if (sellerProfile == null) {
            throw new RuntimeException("User is not a seller");
        }

        if (sellerProfile.isVerified()) {
            throw new RuntimeException("Seller is already verified");
        }

        if (isAdmin) {
            user.getSellerProfile().setVerified(true);
            userRepository.save(user); // Make sure to save the user
        } else {
            throw new RuntimeException("Only admin can verify seller");
        }
    }


}
