package com.naghamtours.repository;

import com.naghamtours.entity.Booking;
import com.naghamtours.entity.Client;
import com.naghamtours.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByClient(Client client);
    List<Booking> findByPackageEntity(Package packageEntity);
    List<Booking> findByBookingDateAfter(LocalDateTime date);
    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.packageEntity LEFT JOIN FETCH b.client LEFT JOIN FETCH b.invoice WHERE b.id = :id AND (b.packageEntity IS NULL OR b.packageEntity.deleted = false)")
    Optional<Booking> findByIdWithPackageAndClient(@Param("id") Long id);
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.packageEntity LEFT JOIN FETCH b.client LEFT JOIN FETCH b.invoice WHERE b.packageEntity IS NULL OR b.packageEntity.deleted = false")
    List<Booking> findAllWithPackageAndClient();
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.packageEntity LEFT JOIN FETCH b.invoice WHERE b.client.id = :userId AND (b.packageEntity IS NULL OR b.packageEntity.deleted = false)")
    List<Booking> findByUserIdWithPackage(@Param("userId") Long userId);
} 