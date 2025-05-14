package com.naghamtours.controller;

import com.naghamtours.entity.Package;
import com.naghamtours.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class TourController {

    private final TourService tourService;

    @Autowired
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Package> createPackage(@RequestBody Package package_) {
        if (package_ == null) {
            return ResponseEntity.badRequest().build();
        }
        if (package_.getStartDate() != null && package_.getEndDate() != null 
            && package_.getStartDate().isAfter(package_.getEndDate())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tourService.savePackage(package_));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Package> getPackageById(@PathVariable Long id) {
        return tourService.getPackageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages() {
        return ResponseEntity.ok(tourService.getAllPackages());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Package>> getUpcomingPackages() {
        return ResponseEntity.ok(tourService.findUpcomingPackages());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Package>> searchPackages(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (destination != null && !destination.trim().isEmpty()) {
            return ResponseEntity.ok(tourService.searchByDestination(destination));
        }
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(tourService.findUpcomingPackages());
        }
        return ResponseEntity.ok(tourService.getAllPackages());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Package> updatePackage(@PathVariable Long id, @RequestBody Package package_) {
        if (package_ == null) {
            return ResponseEntity.badRequest().build();
        }
        if (package_.getStartDate() != null && package_.getEndDate() != null 
            && package_.getStartDate().isAfter(package_.getEndDate())) {
            return ResponseEntity.badRequest().build();
        }
        return tourService.getPackageById(id)
                .map(existingPackage -> {
                    package_.setId(id);
                    return ResponseEntity.ok(tourService.savePackage(package_));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        return tourService.getPackageById(id)
                .map(package_ -> {
                    tourService.deletePackage(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkPackageAvailability(
            @PathVariable Long id,
            @RequestParam int participants) {
        if (participants <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return tourService.getPackageById(id)
                .map(package_ -> ResponseEntity.ok(tourService.isPackageAvailable(package_, participants)))
                .orElse(ResponseEntity.notFound().build());
    }
} 