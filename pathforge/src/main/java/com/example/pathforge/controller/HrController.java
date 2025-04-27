package com.example.pathforge.controller;

import com.example.pathforge.model.Employee;
import com.example.pathforge.model.User;
import com.example.pathforge.service.EmployeeService;
import com.example.pathforge.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;

@Controller
@RequestMapping("/hr")
public class HrController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String hrDashboard(HttpSession session, Model model) {
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

        return "hr";
    }

    @GetMapping("/employees/addEmployee")
    public String showAddEmployeeForm(Model model, HttpSession session) {
        // Add user info from session to the model
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
        model.addAttribute("employee", new Employee());
        return "add_employee_new";
    }

    @PostMapping("/employees/addEmployee")
    public String addEmployee(
            @RequestParam("empId") String empId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("position") String position,
            @RequestParam("qualification") String qualification,
            Model model) {
        try {
            Employee.Qualification qualEnum = Employee.Qualification.valueOf(qualification.toUpperCase());
            Employee employee = new Employee(empId, firstName, lastName, email, position, qualEnum);
            employeeService.saveEmployee(employee);
            return "redirect:/hr";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid qualification. Must be one of: DIPLOMA, BACHELOR, MASTERS, PHD.");
            return "add_employee_new";
        } catch (Exception e) {
            model.addAttribute("error", "Error adding employee: " + e.getMessage());
            return "add_employee_new";
        }
    }

    @GetMapping("/employees/editEmployee/{id}")
    public String showEditEmployeeForm(@PathVariable("id") String empId, Model model, HttpSession session) {
        Employee employee = employeeService.findEmployeeById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + empId));

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
        model.addAttribute("employee", employee);
        return "edit_employee_new";
    }

    @PostMapping("/employees/editEmployee/{id}")
    public String editEmployee(
            @PathVariable("id") String empId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("position") String position,
            @RequestParam("qualification") String qualification,
            Model model) {
        try {
            Employee employee = employeeService.findEmployeeById(empId)
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + empId));
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setPosition(position);
            employee.setQualification(Employee.Qualification.valueOf(qualification.toUpperCase()));
            employeeService.saveEmployee(employee);
            return "redirect:/hr";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid qualification. Must be one of: DIPLOMA, BACHELOR, MASTERS, PHD.");
            model.addAttribute("employee", new Employee(empId, firstName, lastName, email, position, null));
            return "edit_employee_new";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating employee: " + e.getMessage());
            model.addAttribute("employee", new Employee(empId, firstName, lastName, email, position, null));
            return "edit_employee_new";
        }
    }

    @DeleteMapping("/employees/deleteEmployee/{empId}")
    @PreAuthorize("hasRole('HR')")
    public String deleteEmployee(@PathVariable String empId, RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(empId);
        redirectAttributes.addFlashAttribute("message", "Employee deleted successfully");
        return "redirect:/hr";
    }
}