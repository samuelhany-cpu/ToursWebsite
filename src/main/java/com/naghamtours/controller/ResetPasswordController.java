package com.naghamtours.controller;

import com.naghamtours.entity.ResetToken;
import com.naghamtours.entity.User;
import com.naghamtours.entity.Client;
import com.naghamtours.repository.ResetTokenRepository;
import com.naghamtours.repository.ClientRepository;
import com.naghamtours.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class ResetPasswordController {

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired token.");
            return "redirect:/login";
        }
        
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, 
                                     @RequestParam("password") String password,
                                     RedirectAttributes redirectAttributes) {
        ResetToken resetToken = resetTokenRepository.findByToken(token);
        
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/login";
        }

        // Extract email and username from the token
        String[] parts = resetToken.getEmail().split(":", 2);
        String email = parts[0];
        String username = parts.length > 1 ? parts[1] : null;
        boolean resetDone = false;
        if (username != null) {
            User user = userService.findByUsername(username);
            if (user != null && user.getEmail().equalsIgnoreCase(email)) {
                user.setPassword(passwordEncoder.encode(password));
                userService.saveUser(user);
                resetDone = true;
            } else {
                Client client = clientRepository.findByClientName(username).orElse(null);
                if (client != null && client.getClientEmail().equalsIgnoreCase(email)) {
                    client.setClientPassword(passwordEncoder.encode(password));
                    clientRepository.save(client);
                    resetDone = true;
                }
            }
        }
        if (!resetDone) {
            redirectAttributes.addFlashAttribute("error", "User not found or email/username mismatch.");
            return "redirect:/login";
        }
        // Delete the used token
        resetTokenRepository.delete(resetToken);
        redirectAttributes.addFlashAttribute("message", "Your password has been reset successfully. Please login with your new password.");
        return "redirect:/login";
    }
} 