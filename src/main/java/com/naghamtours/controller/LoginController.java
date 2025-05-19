package com.naghamtours.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling login-related views and functionality.
 * Provides endpoints for user authentication and login page rendering.
 */
@Controller
public class LoginController {

    /**
     * Displays the login page
     * @return The name of the login view template
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}