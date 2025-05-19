package com.naghamtours.controller;

import com.naghamtours.entity.Package;
import com.naghamtours.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private PackageRepository packageRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Package> packages = packageRepository.findByEnabledTrue();
        model.addAttribute("packages", packages);
        return "index";
    }
} 