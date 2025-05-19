package com.naghamtours.controller;

import com.naghamtours.entity.Booking;
import com.naghamtours.entity.Package;
import com.naghamtours.exception.ResourceNotFoundException;
import com.naghamtours.service.BookingService;
import com.naghamtours.service.PackageService;
import com.naghamtours.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for managing tour packages in the admin interface and providing API endpoints.
 * Handles both web views and REST API endpoints for tour package management.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTourController {

    @Autowired
    private TourService tourService;

    @Autowired
    private PackageService packageService;

    @Autowired
    private BookingService bookingService;

    /* ========== ADMIN VIEW ENDPOINTS ========== */

    @GetMapping("/tours")
    public String listTours(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String status,
            Model model,
            CsrfToken csrfToken) {
        
        List<Package> tours = tourService.getAllPackages();

        // Apply search filter
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            tours = tours.stream()
                    .filter(tour -> tour.getName().toLowerCase().contains(searchLower) ||
                            tour.getDescription().toLowerCase().contains(searchLower) ||
                            tour.getDestination().toLowerCase().contains(searchLower))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Apply location filter
        if (location != null && !location.isEmpty()) {
            tours = tours.stream()
                    .filter(tour -> tour.getDestination().equalsIgnoreCase(location))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Apply status filter
        if (status != null && !status.isEmpty()) {
            try {
                // Convert string "true"/"false" to Boolean
                Boolean enabled = "true".equalsIgnoreCase(status);
                System.out.println("DEBUG: Status parameter: '" + status + "'");
                System.out.println("DEBUG: Converted to enabled: " + enabled);
                System.out.println("DEBUG: Total tours before filter: " + tours.size());
                
                // Debug: Print all tours and their enabled status
                System.out.println("DEBUG: All tours before filtering:");
                tours.forEach(tour -> {
                    System.out.println("DEBUG: Tour ID: " + tour.getId() + 
                                     ", Name: " + tour.getName() + 
                                     ", Enabled: " + tour.getEnabled() + 
                                     ", Enabled type: " + (tour.getEnabled() != null ? tour.getEnabled().getClass().getName() : "null"));
                });
                
                tours = tours.stream()
                        .filter(tour -> {
                            Boolean tourEnabled = tour.getEnabled();
                            // For inactive filter (enabled=false), we want to match both false and null values
                            if (!enabled) {
                                return tourEnabled == null || !tourEnabled;
                            }
                            // For active filter (enabled=true), we want to match only true values
                            return tourEnabled != null && tourEnabled;
                        })
                        .collect(java.util.stream.Collectors.toList());
                
                System.out.println("DEBUG: Filtered tours count: " + tours.size());
                if (tours.isEmpty()) {
                    System.out.println("DEBUG: No tours matched the filter criteria");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Error in status filter: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Apply sorting
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "name":
                    tours.sort((t1, t2) -> t1.getName().compareTo(t2.getName()));
                    break;
                case "price":
                    tours.sort((t1, t2) -> t1.getPrice().compareTo(t2.getPrice()));
                    break;
                case "duration":
                    tours.sort((t1, t2) -> t1.getDuration().compareTo(t2.getDuration()));
                    break;
                case "startDate":
                    tours.sort((t1, t2) -> t1.getStartDate().compareTo(t2.getStartDate()));
                    break;
            }
        }

        // Get unique locations for the filter dropdown
        List<String> locations = tours.stream()
                .map(Package::getDestination)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        // Get all bookings
        List<Booking> bookings = bookingService.getAllBookings();

        model.addAttribute("tours", tours);
        model.addAttribute("bookings", bookings);
        model.addAttribute("locations", locations);
        model.addAttribute("currentSort", sort);
        model.addAttribute("_csrf", csrfToken);
        return "admin/tours/list";
    }

    @GetMapping("/tours/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tour", new Package());
        return "admin/tours/form";
    }

    @GetMapping("/tours/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Package tour = packageService.getPackageById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        model.addAttribute("tour", tour);
        return "admin/tours/form";
    }

    @PostMapping("/tours")
    public String saveTour(@ModelAttribute Package tour, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("DEBUG: Saving tour: " + tour.getName());
            System.out.println("DEBUG: Tour enabled status: " + tour.getEnabled());
            System.out.println("DEBUG: Tour location: " + tour.getLocation());
            
            // Ensure enabled is never null
            if (tour.getEnabled() == null) {
                tour.setEnabled(false);
            }
            
            // Ensure location is set
            if (tour.getLocation() == null || tour.getLocation().trim().isEmpty()) {
                tour.setLocation(tour.getDestination());
            }
            
            // Set end date based on duration if not set
            if (tour.getStartDate() != null && tour.getDuration() != null && tour.getEndDate() == null) {
                tour.setEndDate(tour.getStartDate().plusDays(tour.getDuration()));
            }
            
            // Save the tour
            Package savedTour = packageService.savePackage(tour);
            System.out.println("DEBUG: Tour saved with ID: " + savedTour.getId());
            
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Tour saved successfully");
        } catch (Exception e) {
            System.out.println("DEBUG: Error saving tour: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("message", "Error saving tour: " + e.getMessage());
        }
        return "redirect:/admin/tours";
    }

    @PostMapping("/tours/{id}/edit")
    public String updateTour(@PathVariable Long id, @ModelAttribute Package tour, RedirectAttributes redirectAttributes) {
        try {
            // Get existing tour to preserve any fields not in the form
            Package existingTour = packageService.getPackageById(id)
                    .orElseThrow(() -> new RuntimeException("Tour not found"));
            
            // Update only the fields that are in the form
            existingTour.setName(tour.getName());
            existingTour.setDescription(tour.getDescription());
            existingTour.setDestination(tour.getDestination());
            existingTour.setPrice(tour.getPrice());
            existingTour.setDuration(tour.getDuration());
            existingTour.setMaxParticipants(tour.getMaxParticipants());
            existingTour.setStartDate(tour.getStartDate());
            existingTour.setEnabled(tour.getEnabled());
            
            // Set end date based on duration if not set
            if (existingTour.getStartDate() != null && existingTour.getDuration() != null && existingTour.getEndDate() == null) {
                existingTour.setEndDate(existingTour.getStartDate().plusDays(existingTour.getDuration()));
            }
            
            packageService.savePackage(existingTour);
            redirectAttributes.addFlashAttribute("successMessage", "Tour updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating tour: " + e.getMessage());
        }
        return "redirect:/admin/tours";
    }

    @PostMapping("/tours/{id}/delete")
    public String deleteTour(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            packageService.deletePackage(id);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Tour deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("message", "Error deleting tour: " + e.getMessage());
            e.printStackTrace(); // Add this for debugging
        }
        return "redirect:/admin/tours";
    }

    @PostMapping("/bookings/{id}/confirm")
    @ResponseBody
    public ResponseEntity<String> confirmBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (booking.getStatus() != Booking.BookingStatus.PENDING) {
                throw new RuntimeException("Only pending bookings can be confirmed");
            }

            bookingService.updateBookingStatus(id, Booking.BookingStatus.CONFIRMED);
            return ResponseEntity.ok("Booking confirmed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bookings/{id}/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                throw new RuntimeException("Booking is already cancelled");
            }
            if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
                throw new RuntimeException("Cannot cancel a completed booking");
            }

            bookingService.cancelBooking(id);
            return ResponseEntity.ok("Booking cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* ========== API ENDPOINTS ========== */

    /**
     * Get all tour packages
     * @return List of all tour packages
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Package>> getAllPackagesApi() {
        return ResponseEntity.ok(tourService.getAllPackages());
    }

    /**
     * Get a specific tour package by ID
     * @param id The ID of the tour package
     * @return The tour package if found, 404 if not found
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Package> getPackageByIdApi(@PathVariable Long id) {
        return tourService.getPackageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new tour package
     * @param tourPackage The tour package to create
     * @return The created tour package
     */
    @PostMapping("/api")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @ResponseBody
    public ResponseEntity<Package> createPackageApi(@RequestBody Package tourPackage) {
        if (tourPackage == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!isValidDateRange(tourPackage.getStartDate(), tourPackage.getEndDate())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tourService.savePackage(tourPackage));
    }

    /**
     * Get all upcoming tour packages
     * @return List of upcoming tour packages
     */
    @GetMapping("/api/upcoming")
    @ResponseBody
    public ResponseEntity<List<Package>> getUpcomingPackagesApi() {
        return ResponseEntity.ok(tourService.findUpcomingPackages());
    }

    /**
     * Search tour packages by destination and/or date range
     * @param destination The destination to search for
     * @param startDate The start date of the search range
     * @param endDate The end date of the search range
     * @return List of matching tour packages
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<Package>> searchPackagesApi(
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
    @PutMapping("/api/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @ResponseBody
    public ResponseEntity<Package> updatePackageApi(@PathVariable Long id, @RequestBody Package tourPackage) {
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
    @DeleteMapping("/api/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Void> deletePackageApi(@PathVariable Long id) {
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
    @GetMapping("/api/{id}/availability")
    @ResponseBody
    public ResponseEntity<Boolean> checkPackageAvailabilityApi(
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