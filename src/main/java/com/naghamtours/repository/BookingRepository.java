package com.naghamtours.repository;

import com.naghamtours.entity.Booking;
import com.naghamtours.entity.Client;
import com.naghamtours.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByClient(Client client);
    List<Booking> findByPackageEntity(Package packageEntity);
    List<Booking> findByBookingDateAfter(LocalDateTime date);
    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    @Query("SELECT b FROM Booking b JOIN FETCH b.packageEntity WHERE b.client.id = :userId")
    List<Booking> findByUserIdWithPackage(Long userId);
    @Query("SELECT b FROM Booking b JOIN FETCH b.packageEntity JOIN FETCH b.client")
    List<Booking> findAllWithPackageAndClient();
} 