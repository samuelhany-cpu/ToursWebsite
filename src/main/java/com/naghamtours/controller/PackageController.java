package com.naghamtours.controller;

import com.naghamtours.entity.Package;
import com.naghamtours.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/packages")
public class PackageController {

    @Autowired
    private PackageRepository packageRepository;

    @GetMapping
    public String listPackages(Model model) {
        List<Package> packages = packageRepository.findAll();
        model.addAttribute("packages", packages);
        return "packages/list";
    }
} 