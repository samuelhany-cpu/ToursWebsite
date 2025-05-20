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
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReservationController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private PackageService packageService;

    @GetMapping("/reservations")
    public String listReservations(Model model) {
        try {
            System.out.println("Attempting to fetch all bookings...");
            List<Booking> bookings = bookingService.getAllBookings();
            System.out.println("Successfully fetched " + bookings.size() + " bookings");
            model.addAttribute("bookings", bookings);
            model.addAttribute("error", null);
            return "admin/reservations";
        } catch (Exception e) {
            System.err.println("Error in listReservations: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load reservations: " + e.getMessage());
            model.addAttribute("bookings", new ArrayList<>());
            return "admin/reservations";
        }
    }

    @GetMapping("/reservations/new")
    public String newReservationForm(Model model) {
        List<Client> clients = userService.getAllClients();
        List<Package> tours = packageService.getAllPackages();
        model.addAttribute("clients", clients);
        model.addAttribute("tours", tours);
        return "admin/reservations/new";
    }

    @PostMapping("/reservations/new")
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
            booking.setPaymentMethod(Booking.PaymentMethod.CASH);
            booking.setPaymentStatus(Booking.PaymentStatus.PENDING);

            bookingService.createBooking(booking);
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Reservation created successfully");
            return "redirect:/admin/reservations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create reservation: " + e.getMessage());
            return "redirect:/admin/reservations/new";
        }
    }

    @PostMapping("/reservations/{id}/confirm-payment")
    @ResponseBody
    public ResponseEntity<String> confirmPayment(@PathVariable Long id) {
        try {
            Booking booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (booking.getPaymentMethod() != Booking.PaymentMethod.CASH) {
                throw new RuntimeException("Only cash payments can be confirmed manually");
            }

            if (booking.getPaymentStatus() == Booking.PaymentStatus.PAID) {
                throw new RuntimeException("Payment is already confirmed");
            }

            bookingService.updatePaymentStatus(id, Booking.PaymentStatus.PAID);
            return ResponseEntity.ok("Payment confirmed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reservations/{id}/confirm")
    @ResponseBody
    public ResponseEntity<String> confirmBooking(@PathVariable Long id, HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("[DEBUG] User: " + (authentication != null ? authentication.getName() : "null"));
            CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
            System.out.println("[DEBUG] CSRF Token: " + (csrf != null ? csrf.getToken() : "null"));
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

    @PostMapping("/reservations/{id}/cancel")
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