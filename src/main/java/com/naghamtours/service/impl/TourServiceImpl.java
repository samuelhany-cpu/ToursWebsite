package com.naghamtours.service.impl;

import com.naghamtours.entity.Package;
import com.naghamtours.repository.PackageRepository;
import com.naghamtours.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TourServiceImpl implements TourService {

    @Autowired
    private PackageRepository packageRepository;

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    @Override
    public Optional<Package> getPackageById(Long id) {
        return packageRepository.findById(id);
    }

    @Override
    public Package savePackage(Package package_) {
        return packageRepository.save(package_);
    }

    @Override
    public void deletePackage(Long id) {
        packageRepository.deleteById(id);
    }

    @Override
    public List<Package> findActivePackages() {
        return packageRepository.findByEnabledTrue();
    }

    @Override
    public List<Package> searchByDestination(String destination) {
        return packageRepository.findByLocationContainingIgnoreCase(destination);
    }

    @Override
    public List<Package> findUpcomingPackages() {
        return packageRepository.findByStartDateAfter(LocalDateTime.now());
    }

    @Override
    public List<Package> findByPriceRange(double maxPrice) {
        return packageRepository.findByPriceLessThanEqual(maxPrice);
    }

    @Override
    public List<Package> findByMinParticipants(int minParticipants) {
        return packageRepository.findByMaxParticipantsGreaterThanEqual(minParticipants);
    }

    @Override
    public boolean isPackageAvailable(Package package_, int participants) {
        return package_.getMaxParticipants() >= participants;
    }
} 