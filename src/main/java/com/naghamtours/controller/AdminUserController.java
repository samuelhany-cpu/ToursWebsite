package com.naghamtours.controller;

import com.naghamtours.entity.Client;
import com.naghamtours.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listClients(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "enabled", required = false) String enabled,
            Model model) {
        List<Client> clients = userService.getAllClients();

        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.trim().toLowerCase();
            clients = clients.stream()
                    .filter(c -> (c.getClientName() != null && c.getClientName().toLowerCase().contains(searchLower))
                            || (c.getClientEmail() != null && c.getClientEmail().toLowerCase().contains(searchLower)))
                    .collect(Collectors.toList());
        }
        if (type != null && !type.isEmpty()) {
            clients = clients.stream()
                    .filter(c -> c.getClientType() != null && c.getClientType().name().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        if (enabled != null && !enabled.isEmpty()) {
            boolean enabledBool = Boolean.parseBoolean(enabled);
            clients = clients.stream()
                    .filter(c -> c.isEnabled() == enabledBool)
                    .collect(Collectors.toList());
        }

        model.addAttribute("clients", clients);
        model.addAttribute("search", search);
        model.addAttribute("type", type);
        model.addAttribute("enabled", enabled);
        return "admin/users";
    }
} 