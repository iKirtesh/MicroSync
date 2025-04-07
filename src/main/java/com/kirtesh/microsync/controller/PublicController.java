package com.kirtesh.microsync.controller;

import com.kirtesh.microsync.model.Users;
import com.kirtesh.microsync.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class PublicController {

    private final UserService userService;

    public PublicController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseBody
    public String greet(HttpServletRequest request, @RequestParam(name = "token", required = false) String token) {
        String loginUrl = request.getContextPath() + "/login/github";
        String usernamePasswordLoginUrl = request.getContextPath() + "/login";

        String responseContent;

        if (token != null) {
            // Display token and GitHub/login links
            responseContent = String.format(
                    "<div style=\"padding: 10px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9;\">" +
                            "    <p>Microsync is up and running! Session ID: <strong>%s</strong>.</p>" +
                            "    <p>Login successful! JWT Token: <code style=\"background-color: #eee; padding: 5px; border-radius: 3px;\">%s</code></p>" +
                            "    <button onclick=\"copyToClipboard('%s')\" style=\"padding: 10px 20px; border: none; border-radius: 5px; background-color: #007bff; color: white; cursor: pointer;\">Copy Token</button>" +
                            "    <a href=\"%s\" style=\"display: inline-block; margin-left: 10px; padding: 10px 20px; border: none; border-radius: 5px; background-color: #28a745; color: white; text-decoration: none;\">Login with GitHub</a>" +
                            "    <a href=\"%s\" style=\"display: inline-block; margin-left: 10px; padding: 10px 20px; border: none; border-radius: 5px; background-color: #007bff; color: white; text-decoration: none;\">Login with Username/Password</a>" +
                            "    <script>function copyToClipboard(text) {navigator.clipboard.writeText(text);alert('Token copied to clipboard!');}</script>" +
                            "</div>",
                    request.getSession().getId(), token, token, loginUrl, usernamePasswordLoginUrl
            );
        } else {
            // Display login form and GitHub/login links
            responseContent = String.format(
                    "<div style=\"padding: 10px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9;\">" +
                            "    <p>Microsync is up and running! Session ID: <strong>%s</strong>.</p>" +
                            "    <form action=\"/\" method=\"post\">" +
                            "        <label for=\"username\">Username:</label><br>" +
                            "        <input style=\"margin-bottom: 10px; padding: 8px 10px; border-radius: 5px;\"" +
                            " type=\"text\" id=\"username\" name=\"username\"><br>" +

                            "        <label for=\"password\">Password:</label><br>" +
                            "        <input style=\"margin-bottom: 4px; padding: 8px 10px; border-radius: 5px;\" type=\"password\" id=\"password\" name=\"password\"><br><br>" +
                            "        <input style=\"padding: 10px 20px; border: none; border-radius: 5px; background-color: #007bff; color: white; cursor: pointer;\" " +
                            "type=\"submit\" value=\"Login In Account\">" +
                            "    </form>" +
                            "    <a href=\"%s\" style=\"display: inline-block; margin-top: 4px; padding: 10px 20px; border: none; border-radius: 5px; background-color: #007bff; color: white; text-decoration: none;\">Login with GitHub</a>" +
                            "</div>",
                    request.getSession().getId(), loginUrl
            );
        }

        return responseContent;
    }

    @GetMapping("/login/github")
    public String loginWithGitHub() {
        return "redirect:/oauth2/authorization/github";
    }

    @PostMapping
    @ResponseBody
    public String loginAsUser(@RequestParam String username, @RequestParam String password) {
        Users user = new Users();
        user.setUsername(username);
        user.setPassword(password);
        String token = userService.verifyLogin(user);

        if ("fail".equals(token)) {
            return "<div style=\"padding: 10px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9;\">" +
                    "    <p>Authentication failed. Please try again.</p>" +
                    "    <a href=\"/\" style=\"display: inline-block; padding: 10px 20px; border: none; border-radius: 5px; background-color: #007bff; color: white; text-decoration: none;\">Back to Login</a>" +
                    "</div>";
        } else {
            return String.format(
                    "<div style=\"padding: 10px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9;\">" +
                            "    <p>Login successful! JWT Token: <code style=\"background-color: #eee; padding: 5px; border-radius: 3px;\">%s</code></p>" +
                            "    <button onclick=\"copyToClipboard('%s')\" style=\"padding: 10px 20px; border: none; border-radius: 5px; background-color: #007bff; color: white; cursor: pointer;\">Copy Token</button>" +
                            "    <a href=\"/\" style=\"display: inline-block; margin-top: 10px; padding: 10px 20px; border: none; border-radius: 5px; background-color: #28a745; color: white; text-decoration: none;\">Back to Home</a>" +
                            "    <script>function copyToClipboard(text) {navigator.clipboard.writeText(text);alert('Token copied to clipboard!');}</script>" +
                            "</div>",
                    token, token
            );
        }
    }
}
