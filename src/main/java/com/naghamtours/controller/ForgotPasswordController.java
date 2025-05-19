package com.naghamtours.controller;

import com.naghamtours.entity.ResetToken;
import com.naghamtours.repository.ResetTokenRepository;
import com.naghamtours.service.EmailService;
import com.naghamtours.service.UserService;
import com.naghamtours.entity.Client;
import com.naghamtours.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class ForgotPasswordController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       RedirectAttributes redirectAttributes) {
        // Check if username and email match a User
        boolean validUser = userService.existsByUsername(username) && userService.existsByEmail(email);
        boolean userMatch = false;
        if (validUser) {
            var user = userService.findByUsername(username);
            userMatch = user != null && user.getEmail().equalsIgnoreCase(email);
        }
        // Check if username and email match a Client
        boolean validClient = clientRepository.findByClientName(username).isPresent() && clientRepository.findByClientEmail(email).isPresent();
        boolean clientMatch = false;
        if (validClient) {
            var client = clientRepository.findByClientName(username).orElse(null);
            clientMatch = client != null && client.getClientEmail().equalsIgnoreCase(email);
        }
        if (!userMatch && !clientMatch) {
            redirectAttributes.addFlashAttribute("error", "No account found with this username and email combination.");
            return "redirect:/forgot-password";
        }
        // Generate a reset token
        String resetToken = java.util.UUID.randomUUID().toString();
        // Save the token in the database (store both username and email for extra safety)
        ResetToken token = new ResetToken(resetToken, email + ":" + username, LocalDateTime.now().plusHours(24));
        resetTokenRepository.save(token);
        try {
            // Send reset email
            emailService.sendResetEmail(email, resetToken);
            redirectAttributes.addFlashAttribute("message", "A password reset link has been sent to your email.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send reset email. Please try again.");
            return "redirect:/forgot-password";
        }
        return "redirect:/login";
    }
} 