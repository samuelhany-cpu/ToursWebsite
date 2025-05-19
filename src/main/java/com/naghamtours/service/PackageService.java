package com.naghamtours.service;

import com.naghamtours.entity.Package;
import java.util.List;
import java.util.Optional;

public interface PackageService {
    List<Package> getActivePackages();
    Optional<Package> getPackageById(Long id);
    Package savePackage(Package packageEntity);
    void deletePackage(Long id);
    List<Package> getAllPackages();
} 