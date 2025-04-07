package com.kirtesh.microsync.component;

import com.kirtesh.microsync.model.OAuthUser;
import com.kirtesh.microsync.model.Users;
import com.kirtesh.microsync.repository.OAuthUserRepository;
import com.kirtesh.microsync.repository.RoleRepository;
import com.kirtesh.microsync.repository.UserRepository;
import com.kirtesh.microsync.service.JWTService;
import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OAuthUserRepository oAuthUserRepository;
    private final JWTService jwtService;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, RoleRepository roleRepository, OAuthUserRepository oAuthUserRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.oAuthUserRepository = oAuthUserRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String githubId = getAttributeAsString(oAuth2User, "id");
        String email = getAttributeAsString(oAuth2User, "email");
        String username = getAttributeAsString(oAuth2User, "login");

        String name = oAuth2User.getAttribute("name");
        String[] nameParts = name != null ? name.split(" ", 2) : new String[]{"", ""};
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        Optional<OAuthUser> existingOAuthUser = oAuthUserRepository.findByGithubId(githubId);
        Users user;
        if (existingOAuthUser.isPresent()) {
            user = existingOAuthUser.get().getUser();
        } else {
            Optional<Users> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                user = new Users();
                user.setUsername(username);
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmailVerified(true); // Set true since email is verified by OAuth provider
                user.setPassword("N/A"); // Set a default value for the password field
                user.setRoles(Set.of(roleRepository.findByName("USER")
                        .orElseThrow(() -> new RuntimeException("Role not found"))));
                userRepository.save(user);
            }

            OAuthUser oauthUser = new OAuthUser();
            oauthUser.setGithubId(githubId);
            oauthUser.setUser(user);
            oAuthUserRepository.save(oauthUser);
        }


        String token = jwtService.generateToken(user.getUsername());
        Hibernate.initialize(user.getRoles()); // Initialize the proxy
//            response.setHeader("Authorization", "Bearer " + token);

        String redirectUrl = String.format("/?token=%s", token);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String getAttributeAsString(OAuth2User oAuth2User, String attributeName) {
        Object attribute = oAuth2User.getAttribute(attributeName);
        if (attribute == null) {
            throw new RuntimeException("Attribute not found or is null: " + attributeName);
        }
        return attribute.toString();
    }

}
