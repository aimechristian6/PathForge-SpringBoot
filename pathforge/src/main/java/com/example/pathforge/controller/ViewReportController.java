package com.example.pathforge.controller;

import com.example.pathforge.model.User;
import com.example.pathforge.service.EmployeeService;
import com.example.pathforge.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Base64;

@Controller
public class ViewReportController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/viewReports")
    public String viewReports(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        User user = userService.findByUsername(username);
        String profilePictureBase64 = user != null && user.getProfilePicture() != null
                ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getProfilePicture())
                : "/images/default-profile.jpg";

        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("profilePictureBase64", profilePictureBase64);
        model.addAttribute("employees", employeeService.findAllEmployees());

        return "viewReports";
    }
}