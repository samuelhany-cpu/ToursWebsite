package com.naghamtours.controller;

import com.naghamtours.entity.Booking;
import com.naghamtours.entity.Client;
import com.naghamtours.entity.Package;
import com.naghamtours.service.BookingService;
import com.naghamtours.service.UserService;
import com.naghamtours.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    @Autowired
    private TourService tourService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try {
            // Get total active tours
            List<Package> activeTours = tourService.getAllActivePackages();
            model.addAttribute("totalTours", activeTours.size());

            // Get all bookings and filter out those with missing or deleted packages
            List<Booking> allBookings = bookingService.getAllBookings();
            List<Booking> validBookings = allBookings.stream()
                .filter(booking -> {
                    try {
                        Package package_ = booking.getPackageEntity();
                        return package_ != null && !package_.getDeleted();
                    } catch (Exception e) {
                        logger.warn("Error processing booking {}: {}", booking.getId(), e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());

            // Calculate active reservations (PENDING or CONFIRMED)
            long activeReservations = validBookings.stream()
                .filter(booking -> booking.getStatus() == Booking.BookingStatus.PENDING || 
                                booking.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();
            model.addAttribute("activeReservations", activeReservations);

            // Calculate monthly revenue from confirmed bookings in current month
            double monthlyRevenue = validBookings.stream()
                .filter(booking -> {
                    try {
                        Package package_ = booking.getPackageEntity();
                        return booking.getStatus() == Booking.BookingStatus.CONFIRMED &&
                               booking.getBookingDate().getMonth() == LocalDateTime.now().getMonth() &&
                               booking.getBookingDate().getYear() == LocalDateTime.now().getYear() &&
                               package_ != null && !package_.getDeleted();
                    } catch (Exception e) {
                        logger.warn("Error calculating revenue for booking {}: {}", booking.getId(), e.getMessage());
                        return false;
                    }
                })
                .mapToDouble(booking -> booking.getPackageEntity().getPrice())
                .sum();
            model.addAttribute("monthlyRevenue", monthlyRevenue);

            // Get recent bookings (last 5) with valid packages
            List<Booking> recentBookings = validBookings.stream()
                .sorted((b1, b2) -> b2.getBookingDate().compareTo(b1.getBookingDate()))
                .limit(5)
                .collect(Collectors.toList());
            model.addAttribute("recentBookings", recentBookings);

            return "admin/dashboard";
        } catch (Exception e) {
            logger.error("Error loading dashboard: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error loading dashboard. Please try again later.");
        }
    }
}