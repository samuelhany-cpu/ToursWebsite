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
import com.naghamtours.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PackageServiceImpl implements PackageService {

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
    public List<Package> getActivePackages() {
        LocalDateTime now = LocalDateTime.now();
        return packageRepository.findByStartDateAfter(now);
    }

    @Override
    public Optional<Package> getPackageById(Long id) {
        return packageRepository.findById(id);
    }

    @Override
    public Package savePackage(Package packageEntity) {
        // Handle enabled status
        if (packageEntity.getEnabled() == null) {
            packageEntity.setEnabled(false);
        }
        // Set end date based on duration if not set
        if (packageEntity.getStartDate() != null && packageEntity.getDuration() != null && packageEntity.getEndDate() == null) {
            packageEntity.setEndDate(packageEntity.getStartDate().plusDays(packageEntity.getDuration()));
        }
        return packageRepository.save(packageEntity);
    }

    @Override
    @Transactional
    public void deletePackage(Long id) {
        try {
            Package package_ = packageRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Package not found with id: " + id));
            
            // Mark the package as deleted
            package_.setDeleted(true);
            packageRepository.save(package_);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting package: " + e.getMessage());
        }
    }

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }
} 