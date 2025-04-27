package com.example.pathforge.controller;

import com.example.pathforge.model.User;
import com.example.pathforge.repository.UserRepository;
import com.example.pathforge.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Homepage
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Show signup page
    @GetMapping("/signup")
    public String showSignupForm() {
        return "signup";
    }

    // Handle signup form submission
    @PostMapping("/signup")
    public String signup(
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("profilePicture") MultipartFile profilePicture,
            @RequestParam("role") String role,
            @RequestParam("password") String password,
            Model model
    ) {
        try {
            // Create a new user
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setProfilePicture(profilePicture.getBytes());
            user.setRole(role.toUpperCase());
            // Hash the password before saving
            user.setPassword(passwordEncoder.encode(password));

            // Save the user to the database
            userRepository.save(user);
            logger.info("User signed up successfully: email={}, username={}, role={}", email, username, role);

            // Redirect to login page after successful signup
            return "redirect:/login?signupSuccess=true";
        } catch (Exception e) {
            logger.error("Error during signup for email={}: {}", email, e.getMessage(), e);
            model.addAttribute("error", "Error during signup: " + e.getMessage());
            return "signup";
        }
    }

    // Show login page
    @GetMapping("/login")
    public String showLoginForm(Model model,
                                @RequestParam(value = "signupSuccess", required = false) Boolean signupSuccess,
                                @RequestParam(value = "error", required = false) Boolean error,
                                @RequestParam(value = "logout", required = false) Boolean logout) {
        if (signupSuccess != null && signupSuccess) {
            model.addAttribute("message", "Signup successful! Please log in.");
        }
        if (error != null && error) {
            model.addAttribute("error", "Invalid email or password.");
        }
        if (logout != null && logout) {
            model.addAttribute("message", "You have been logged out.");
        }
        return "login";
    }
}