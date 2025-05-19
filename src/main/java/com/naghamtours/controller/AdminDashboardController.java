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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private TourService tourService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get total tours
        List<Package> allTours = tourService.getAllPackages();
        model.addAttribute("totalTours", allTours.size());

        // Get active reservations (bookings with PENDING or CONFIRMED status)
        List<Booking> allBookings = bookingService.getAllBookings();
        long activeReservations = allBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.PENDING || 
                           b.getStatus() == Booking.BookingStatus.CONFIRMED)
                .count();
        model.addAttribute("activeReservations", activeReservations);

        // Get total clients
        List<Client> allClients = userService.getAllClients();
        model.addAttribute("totalClients", allClients.size());

        // Calculate monthly revenue (sum of all confirmed bookings in the current month)
        BigDecimal monthlyRevenue = allBookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED &&
                           b.getBookingDate().getMonth() == LocalDateTime.now().getMonth() &&
                           b.getBookingDate().getYear() == LocalDateTime.now().getYear())
                .map(b -> {
                    BigDecimal price = BigDecimal.valueOf(b.getPackageEntity().getPrice());
                    BigDecimal participants = new BigDecimal(b.getParticipants());
                    return price.multiply(participants);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("monthlyRevenue", monthlyRevenue);

        // Get recent activities (last 5 bookings)
        List<Booking> recentBookings = allBookings.stream()
                .sorted((b1, b2) -> b2.getBookingDate().compareTo(b1.getBookingDate()))
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentBookings", recentBookings);

        return "admin/dashboard";
    }
}