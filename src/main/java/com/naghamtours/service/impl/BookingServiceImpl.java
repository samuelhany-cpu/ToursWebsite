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
import java.util.ArrayList;
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
        // Calculate total amount based on package price and number of participants
        Double packagePrice = booking.getPackageEntity().getPrice();
        Integer participants = booking.getParticipants();
        if (packagePrice == null || participants == null) {
            throw new IllegalArgumentException("Package price and number of participants must not be null");
        }
        booking.setTotalAmount(java.math.BigDecimal.valueOf(packagePrice * participants));
        booking.setStatus(Booking.BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findByIdWithPackageAndClient(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        try {
            System.out.println("Starting to fetch all bookings...");
            // First try the basic query
            List<Booking> bookings = bookingRepository.findAllBasic();
            System.out.println("Basic query returned " + (bookings != null ? bookings.size() : 0) + " bookings");
            
            if (bookings == null || bookings.isEmpty()) {
                System.out.println("No bookings found with basic query");
                return new ArrayList<>();
            }

            // Now try to load the related entities
            try {
                System.out.println("Attempting to load related entities...");
                bookings = bookingRepository.findAllWithPackageAndClientWithoutInvoice();
                System.out.println("Successfully loaded " + bookings.size() + " bookings with related entities");
            } catch (Exception e) {
                System.err.println("Error loading related entities: " + e.getMessage());
                // If loading related entities fails, return the basic bookings
                System.out.println("Returning basic booking data");
                return bookings;
            }

            return bookings;
        } catch (Exception e) {
            System.err.println("Error in getAllBookings: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch bookings: " + e.getMessage(), e);
        }
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
    public Booking updatePaymentStatus(Long id, Booking.PaymentStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setPaymentStatus(status);
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