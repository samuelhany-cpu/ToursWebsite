package com.naghamtours.controller;

import com.naghamtours.entity.Client;
import com.naghamtours.entity.Employee;
import com.naghamtours.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserRegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/signup", "/register"})
    public String showSignupForm(Model model) {
        model.addAttribute("client", new Client());
        return "signup";
    }

    @GetMapping("/employee-signup")
    public String showEmployeeSignupForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee-signup";
    }

    @PostMapping("/register/employee")
    public String registerEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "employee-signup";
        }

        // Check if username already exists
        if (userService.existsByUsername(employee.getEmpName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username already exists");
            return "redirect:/employee-signup";
        }

        // Check if email already exists
        if (userService.existsByEmail(employee.getEmpEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email already exists");
            return "redirect:/employee-signup";
        }

        try {
            // Set default role and enabled status
            employee.setRole(Employee.Role.ROLE_EMPLOYEE);
            employee.setEnabled(true);
            
            // Encode the password before saving
            employee.setEmpPassword(passwordEncoder.encode(employee.getEmpPassword()));
            
            userService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed. Please try again.");
            return "redirect:/employee-signup";
        }
    }

    @PostMapping({"/signup", "/register"})
    public String registerUser(@Valid @ModelAttribute("client") Client client,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "signup";
        }

        // Check if username already exists
        if (userService.existsByUsername(client.getClientName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username already exists");
            return "redirect:/signup";
        }

        // Check if email already exists
        if (userService.existsByEmail(client.getClientEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email already exists");
            return "redirect:/signup";
        }

        try {
            // Set default client type and enabled status
            client.setClientType(Client.ClientType.REGULAR);
            client.setEnabled(true);
            
            // Encode the password before saving
            client.setClientPassword(passwordEncoder.encode(client.getClientPassword()));
            
            userService.saveClient(client);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed. Please try again.");
            return "redirect:/signup";
        }
    }
} 