package com.naghamtours.service.impl;

import com.naghamtours.entity.Package;
import com.naghamtours.entity.Reserve;
import com.naghamtours.entity.PackageOption;
import com.naghamtours.entity.Cancel;
import com.naghamtours.entity.Booking;
import com.naghamtours.entity.ClientTransactionPayment;
import com.naghamtours.repository.PackageRepository;
import com.naghamtours.repository.PackageOptionRepository;
import com.naghamtours.repository.ReserveRepository;
import com.naghamtours.repository.ClientTransactionPaymentRepository;
import com.naghamtours.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TourServiceImpl implements TourService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private PackageOptionRepository packageOptionRepository;

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private ClientTransactionPaymentRepository clientTransactionPaymentRepository;

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findByDeletedFalse();
    }

    @Override
    public Optional<Package> getPackageById(Long id) {
        return packageRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Package savePackage(Package package_) {
        if (package_.getDeleted() == null) {
            package_.setDeleted(false);
        }
        return packageRepository.save(package_);
    }

    @Override
    @Transactional
    public void deletePackage(Long id) {
        softDeletePackage(id);
    }

    @Override
    public void softDeletePackage(Long id) {
        Package package_ = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
        
        // Mark the package as deleted
        package_.setDeleted(true);
        packageRepository.save(package_);
    }

    @Override
    public List<Package> getAllActivePackages() {
        return packageRepository.findByDeletedFalseAndEnabledTrue();
    }

    @Override
    public Optional<Package> getActivePackageById(Long id) {
        return packageRepository.findByIdAndDeletedFalse(id)
                .filter(p -> p.getEnabled());
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

    @Override
    public List<Package> findPackagesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return packageRepository.findByStartDateBetween(startDate, endDate);
    }
} 