package com.example.pathforge.controller;

import com.example.pathforge.model.User;
import com.example.pathforge.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {
        // Get logged-in user details from session
        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        // Fetch the user to get the profile picture
        User user = userService.findByUsername(username);
        String profilePictureBase64 = user != null && user.getProfilePicture() != null
                ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getProfilePicture())
                : "/images/default-profile.jpg"; // Fallback image

        // Add user details to the model
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("profilePictureBase64", profilePictureBase64);

        // Add active admins count
        int activeAdminsCount = userService.getActiveAdminsCount();
        model.addAttribute("activeAdminsCount", activeAdminsCount);

        // Add active users list
        List<User> activeUsers = userService.getActiveUsers().values().stream().collect(Collectors.toList());
        model.addAttribute("activeUsers", activeUsers);

        return "admin";
    }
}