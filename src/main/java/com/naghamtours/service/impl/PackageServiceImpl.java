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

            // First, get all reserves for this package
            List<Reserve> reserves = reserveRepository.findByPackageEntity(package_);

            // For each reserve, handle its relationships
            for (Reserve reserve : reserves) {
                // First, delete any client transaction payments
                ClientTransactionPayment payment = reserve.getPayment();
                if (payment != null) {
                    // Delete the payment first
                    clientTransactionPaymentRepository.delete(payment);
                    // Clear the reference
                    reserve.setPayment(null);
                    entityManager.persist(reserve);
                }
            }

            // Now delete all reserves
            for (Reserve reserve : reserves) {
                entityManager.remove(reserve);
            }

            // Delete package options
            List<PackageOption> options = package_.getPackageOptions();
            if (options != null) {
                for (PackageOption option : options) {
                    entityManager.remove(option);
                }
            }

            // Delete cancels
            List<Cancel> cancels = package_.getCancels();
            if (cancels != null) {
                for (Cancel cancel : cancels) {
                    entityManager.remove(cancel);
                }
            }

            // Delete bookings
            List<Booking> bookings = package_.getBookings();
            if (bookings != null) {
                for (Booking booking : bookings) {
                    entityManager.remove(booking);
                }
            }

            // Finally delete the package
            entityManager.remove(package_);

            // Flush and clear to ensure all changes are applied
            entityManager.flush();
            entityManager.clear();
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