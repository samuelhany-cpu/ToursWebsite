package com.naghamtours.service;

import com.naghamtours.entity.Package;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TourService {
    List<Package> getAllPackages();
    Optional<Package> getPackageById(Long id);
    Package savePackage(Package package_);
    void deletePackage(Long id);
    List<Package> findActivePackages();
    List<Package> searchByDestination(String destination);
    List<Package> findUpcomingPackages();
    List<Package> findByPriceRange(double maxPrice);
    List<Package> findByMinParticipants(int minParticipants);
    boolean isPackageAvailable(Package package_, int participants);
    List<Package> findPackagesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    // Add soft delete methods
    void softDeletePackage(Long id);
    List<Package> getAllActivePackages();
    Optional<Package> getActivePackageById(Long id);
} 