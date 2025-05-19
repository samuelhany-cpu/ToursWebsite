package com.naghamtours.controller;

import com.naghamtours.entity.Booking;
import com.naghamtours.entity.Client;
import com.naghamtours.entity.Package;
import com.naghamtours.service.BookingService;
import com.naghamtours.service.UserService;
import com.naghamtours.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/reservations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReservationController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private PackageService packageService;

    @GetMapping
    public String listReservations(Model model) {
        List<Booking> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        return "admin/reservations";
    }

    @GetMapping("/new")
    public String newReservationForm(Model model) {
        List<Client> clients = userService.getAllClients();
        List<Package> tours = packageService.getAllPackages();
        model.addAttribute("clients", clients);
        model.addAttribute("tours", tours);
        return "admin/reservations/new";
    }

    @PostMapping("/new")
    public String createReservation(@RequestParam Long clientId,
                                  @RequestParam Long tourId,
                                  @RequestParam int participants,
                                  @RequestParam String bookingDate,
                                  RedirectAttributes redirectAttributes) {
        try {
            Client client = userService.getClientById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            Package tour = packageService.getPackageById(tourId)
                    .orElseThrow(() -> new RuntimeException("Tour not found"));

            if (participants > tour.getMaxParticipants()) {
                redirectAttributes.addFlashAttribute("error", "Number of participants exceeds tour capacity");
                return "redirect:/admin/reservations/new";
            }

            Booking booking = new Booking();
            booking.setClient(client);
            booking.setPackageEntity(tour);
            booking.setParticipants(participants);
            booking.setBookingDate(LocalDateTime.parse(bookingDate + "T00:00:00"));
            booking.setStatus(Booking.BookingStatus.PENDING);

            bookingService.createBooking(booking);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Reservation created successfully");
            return "redirect:/admin/reservations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create reservation: " + e.getMessage());
            return "redirect:/admin/reservations/new";
        }
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                             @RequestParam Booking.BookingStatus status,
                             RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, status);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Reservation status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    @PostMapping("/{id}/confirm")
    @ResponseBody
    public ResponseEntity<String> confirmBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (booking.getStatus() != Booking.BookingStatus.PENDING) {
                throw new RuntimeException("Only pending bookings can be confirmed");
            }

            bookingService.updateBookingStatus(id, Booking.BookingStatus.CONFIRMED);
            return ResponseEntity.ok("Booking confirmed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                throw new RuntimeException("Booking is already cancelled");
            }
            if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
                throw new RuntimeException("Cannot cancel a completed booking");
            }

            bookingService.cancelBooking(id);
            return ResponseEntity.ok("Booking cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 