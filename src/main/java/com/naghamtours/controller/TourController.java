package com.naghamtours.controller;

import com.naghamtours.entity.Package;
import com.naghamtours.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.naghamtours.exception.ResourceNotFoundException;
import com.naghamtours.repository.PackageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing tour packages through both web views and REST API endpoints.
 * Provides endpoints for listing, creating, updating, and deleting tour packages.
 */
@Controller
@RequestMapping("/api/packages")
public class TourController {

    private final TourService tourService;
    private final PackageRepository packageRepository;

    @Autowired
    public TourController(TourService tourService, PackageRepository packageRepository) {
        this.tourService = tourService;
        this.packageRepository = packageRepository;
    }

    /* ========== WEB VIEW ENDPOINTS ========== */

    @GetMapping("/list")
    public String listTours(Model model) {
        model.addAttribute("tours", tourService.getAllPackages());
        return "admin/tours/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tour", new Package());
        return "admin/tours/create";
    }

    @PostMapping("/new")
    public String createTour(@ModelAttribute Package tour) {
        tourService.savePackage(tour);
        return "redirect:/api/packages/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Package tour = tourService.getPackageById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + id));
        model.addAttribute("tour", tour);
        return "admin/tours/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateTour(@PathVariable Long id, @ModelAttribute Package tour) {
        tour.setId(id);
        tourService.savePackage(tour);
        return "redirect:/api/packages/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteTour(@PathVariable Long id) {
        tourService.deletePackage(id);
        return "redirect:/api/packages/list";
    }

    /* ========== API ENDPOINTS ========== */

    /**
     * Create a new tour package
     * @param tourPackage The tour package to create
     * @return The created tour package
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Package> createPackage(@RequestBody Package tourPackage) {
        if (tourPackage == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!isValidDateRange(tourPackage.getStartDate(), tourPackage.getEndDate())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tourService.savePackage(tourPackage));
    }

    /**
     * Get a specific tour package by ID
     * @param id The ID of the tour package
     * @return The tour package if found, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Package> getPackageById(@PathVariable Long id) {
        return tourService.getPackageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all tour packages
     * @return List of all tour packages
     */
    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages() {
        return ResponseEntity.ok(tourService.getAllPackages());
    }

    /**
     * Get all upcoming tour packages
     * @return List of upcoming tour packages
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<Package>> getUpcomingPackages() {
        return ResponseEntity.ok(tourService.findUpcomingPackages());
    }

    /**
     * Search tour packages by destination and/or date range
     * @param destination The destination to search for
     * @param startDate The start date of the search range
     * @param endDate The end date of the search range
     * @return List of matching tour packages
     */
    @GetMapping("/search")
    public ResponseEntity<List<Package>> searchPackages(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (destination != null && !destination.trim().isEmpty()) {
            return ResponseEntity.ok(tourService.searchByDestination(destination));
        }
        if (startDate != null && endDate != null) {
            if (!isValidDateRange(startDate, endDate)) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(tourService.findPackagesBetweenDates(startDate, endDate));
        }
        return ResponseEntity.ok(tourService.getAllPackages());
    }

    /**
     * Update an existing tour package
     * @param id The ID of the tour package to update
     * @param tourPackage The updated tour package data
     * @return The updated tour package
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Package> updatePackage(@PathVariable Long id, @RequestBody Package tourPackage) {
        if (tourPackage == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!isValidDateRange(tourPackage.getStartDate(), tourPackage.getEndDate())) {
            return ResponseEntity.badRequest().build();
        }
        return tourService.getPackageById(id)
                .map(existingPackage -> {
                    tourPackage.setId(id);
                    return ResponseEntity.ok(tourService.savePackage(tourPackage));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a tour package
     * @param id The ID of the tour package to delete
     * @return 200 OK if deleted successfully, 404 if not found
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        return tourService.getPackageById(id)
                .map(tourPackage -> {
                    tourService.deletePackage(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Check if a tour package is available for a given number of participants
     * @param id The ID of the tour package
     * @param participants The number of participants to check availability for
     * @return true if available, false if not available, 400 if invalid input, 404 if package not found
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkPackageAvailability(
            @PathVariable Long id,
            @RequestParam int participants) {
        if (participants <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return tourService.getPackageById(id)
                .map(tourPackage -> ResponseEntity.ok(tourService.isPackageAvailable(tourPackage, participants)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Validates if a date range is valid
     * @param startDate The start date
     * @param endDate The end date
     * @return true if the date range is valid, false otherwise
     */
    private boolean isValidDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null && !startDate.isAfter(endDate);
    }
} 