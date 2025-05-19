package com.naghamtours.service.impl;

import com.naghamtours.entity.Booking;
import com.naghamtours.entity.Client;
import com.naghamtours.entity.Package;
import com.naghamtours.repository.BookingRepository;
import com.naghamtours.repository.ClientRepository;
import com.naghamtours.repository.PackageRepository;
import com.naghamtours.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        booking.setStatus(Booking.BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAllWithPackageAndClient();
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserIdWithPackage(userId);
    }

    @Override
    public List<Booking> getBookingsByTourId(Long tourId) {
        Package packageEntity = packageRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        return bookingRepository.findByPackageEntity(packageEntity);
    }

    @Override
    public List<Booking> getUpcomingBookings() {
        return bookingRepository.findByBookingDateAfter(LocalDateTime.now());
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long id, Booking.BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public boolean isBookingPossible(Long tourId, int participants) {
        if (participants <= 0) {
            throw new IllegalArgumentException("Number of participants must be positive");
        }
        Package packageEntity = packageRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Package not found"));
        return packageEntity.getMaxParticipants() >= participants;
    }

    @Override
    public List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return bookingRepository.findByBookingDateBetween(startDate, endDate);
    }
} 