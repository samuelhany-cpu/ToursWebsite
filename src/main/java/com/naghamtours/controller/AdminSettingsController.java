package com.naghamtours.controller;

import com.naghamtours.entity.Admin;
import com.naghamtours.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettingsController {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminSettingsController(AdminService adminService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String settingsPage(Model model, Authentication authentication) {
        List<Admin> admins = adminService.findAll();
        Admin currentAdmin = adminService.findByUsername(authentication.getName());
        
        model.addAttribute("admins", admins);
        model.addAttribute("currentAdmin", currentAdmin);
        return "admin/settings";
    }

    @PostMapping("/add-admin")
    public String addAdmin(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        try {
            // Validate username format
            if (!username.matches("[a-zA-Z0-9_]{3,20}")) {
                throw new IllegalArgumentException("Username must be 3-20 characters long and can only contain letters, numbers, and underscores");
            }

            // Validate password format
            if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
                throw new IllegalArgumentException("Password must be at least 8 characters long and include both letters and numbers");
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match");
            }

            // Check if username already exists
            if (adminService.findAll().stream().anyMatch(a -> a.getUsername().equals(username))) {
                throw new IllegalArgumentException("Username already exists");
            }

            // Check if email already exists
            if (adminService.findAll().stream().anyMatch(a -> a.getEmail().equals(email))) {
                throw new IllegalArgumentException("Email already exists");
            }

            // Create and save the new admin
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setEnabled(true);

            adminService.save(admin);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Admin added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    @GetMapping("/admin/{id}")
    @ResponseBody
    public ResponseEntity<Admin> getAdmin(@PathVariable Long id) {
        return adminService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/update-admin")
    public String updateAdmin(@RequestParam Long id,
                            @RequestParam String username,
                            @RequestParam String email,
                            @RequestParam(required = false) String newPassword,
                            @RequestParam(required = false) String confirmNewPassword,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            // Get current admin
            Admin currentAdmin = adminService.findByUsername(authentication.getName());
            
            // Only allow updating own profile
            if (!currentAdmin.getId().equals(id)) {
                throw new IllegalArgumentException("You can only update your own profile");
            }

            // Check if username is being changed and if it's already taken
            if (!currentAdmin.getUsername().equals(username) && 
                adminService.findAll().stream().anyMatch(a -> a.getUsername().equals(username))) {
                throw new IllegalArgumentException("Username already exists");
            }

            // Check if email is being changed and if it's already taken
            if (!currentAdmin.getEmail().equals(email) && 
                adminService.findAll().stream().anyMatch(a -> a.getEmail().equals(email))) {
                throw new IllegalArgumentException("Email already exists");
            }

            currentAdmin.setUsername(username);
            currentAdmin.setEmail(email);

            if (newPassword != null && !newPassword.isEmpty()) {
                if (!newPassword.equals(confirmNewPassword)) {
                    throw new IllegalArgumentException("New passwords do not match");
                }
                currentAdmin.setPassword(passwordEncoder.encode(newPassword));
            }

            adminService.save(currentAdmin);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    @DeleteMapping("/delete-admin/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id, Authentication authentication) {
        try {
            // Get current admin
            Admin currentAdmin = adminService.findByUsername(authentication.getName());
            
            // Only allow admin "yousef" to delete other admins
            if (!"yousef".equals(currentAdmin.getUsername())) {
                throw new IllegalArgumentException("Only the super admin can delete other admins");
            }
            
            // Prevent self-deletion
            if (currentAdmin.getId().equals(id)) {
                throw new IllegalArgumentException("You cannot delete your own account");
            }

            adminService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/toggle-admin/{id}")
    @ResponseBody
    public ResponseEntity<?> toggleAdminStatus(@PathVariable Long id, 
                                             @RequestBody ToggleRequest request,
                                             Authentication authentication) {
        try {
            // Get current admin
            Admin currentAdmin = adminService.findByUsername(authentication.getName());
            
            // Only allow admin "yousef" to disable/enable other admins
            if (!"yousef".equals(currentAdmin.getUsername())) {
                throw new IllegalArgumentException("Only the super admin can enable/disable other admins");
            }
            
            // Prevent self-disabling
            if (currentAdmin.getId().equals(id)) {
                throw new IllegalArgumentException("You cannot disable your own account");
            }

            Admin admin = adminService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
            admin.setEnabled(request.isEnabled());
            adminService.save(admin);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-profile")
    public String updateProfile(@RequestParam Long id,
                              @RequestParam String username,
                              @RequestParam String email,
                              @RequestParam(required = false) String newPassword,
                              @RequestParam(required = false) String confirmNewPassword,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            // Verify that the admin is updating their own profile
            Admin currentAdmin = adminService.findByUsername(authentication.getName());
            if (!currentAdmin.getId().equals(id)) {
                throw new IllegalArgumentException("You can only update your own profile");
            }

            currentAdmin.setUsername(username);
            currentAdmin.setEmail(email);

            if (newPassword != null && !newPassword.isEmpty()) {
                if (!newPassword.equals(confirmNewPassword)) {
                    throw new IllegalArgumentException("New passwords do not match");
                }
                currentAdmin.setPassword(passwordEncoder.encode(newPassword));
            }

            adminService.save(currentAdmin);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    private static class ToggleRequest {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
} 