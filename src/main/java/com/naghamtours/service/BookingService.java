package com.naghamtours.service;

import com.naghamtours.entity.Booking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking createBooking(Booking booking);
    Optional<Booking> getBookingById(Long id);
    List<Booking> getAllBookings();
    List<Booking> getBookingsByUserId(Long userId);
    List<Booking> getBookingsByTourId(Long tourId);
    List<Booking> getUpcomingBookings();
    Booking updateBookingStatus(Long id, Booking.BookingStatus status);
    void cancelBooking(Long id);
    boolean isBookingPossible(Long tourId, int participants);
    List<Booking> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
} 